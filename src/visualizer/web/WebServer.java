package visualizer.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.AlgorithmFactory;
import visualizer.model.ArrayModel;
import visualizer.model.Role;
import visualizer.model.Step;

/**
 * Optional browser front end. The same Algorithm classes record the steps, and the page
 * in web/index.html plays them back. It's a second "view" on the same model, and none of the
 * algorithm classes had to change for it.
 *
 * Run with:  java -cp out visualizer.web.WebServer       then open http://localhost:8080
 */
public class WebServer {

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/algorithms", WebServer::algorithms);
        server.createContext("/api/run", WebServer::run);
        server.createContext("/", WebServer::page);
        server.start();
        System.out.println("Algorithm Visualizer running at http://localhost:" + port);
    }

    /** GET /api/algorithms : names, complexities and whether a target is needed. */
    private static void algorithms(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder("{\"algorithms\":[");
        String[] names = AlgorithmFactory.names();
        for (int i = 0; i < names.length; i++) {
            Algorithm a = AlgorithmFactory.create(names[i]);
            if (i > 0) sb.append(',');
            sb.append("{\"name\":").append(str(a.getName()))
              .append(",\"complexity\":").append(str(a.complexity()))
              .append(",\"needsTarget\":").append(a.needsTarget()).append('}');
        }
        sb.append("],\"kinds\":[");
        ArrayModel.Kind[] kinds = ArrayModel.Kind.values();
        for (int i = 0; i < kinds.length; i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"id\":").append(str(kinds[i].name())).append(",\"label\":").append(str(kinds[i].toString())).append('}');
        }
        sb.append("]}");
        send(ex, 200, "application/json", sb.toString());
    }

    /**
     * GET /api/run?algo=Merge%20Sort&vs=Heap%20Sort&kind=RANDOM&size=30&target=12
     * Records one algorithm, or two on the same array when `vs` is given (race mode).
     */
    private static void run(HttpExchange ex) throws IOException {
        try {
            Map<String, String> q = query(ex.getRequestURI().getRawQuery());
            List<Algorithm> algos = new ArrayList<>();
            algos.add(AlgorithmFactory.create(q.getOrDefault("algo", "Bubble Sort")));
            String vs = q.getOrDefault("vs", "").trim();
            if (!vs.isEmpty()) algos.add(AlgorithmFactory.create(vs));
            boolean search = algos.get(0).needsTarget();
            for (Algorithm a : algos) {
                if (a.needsTarget() != search) throw new IllegalArgumentException("A race needs two sorts or two searches");
            }

            int size = Math.max(5, Math.min(100, Integer.parseInt(q.getOrDefault("size", "30"))));
            ArrayModel model = new ArrayModel();
            model.generate(ArrayModel.Kind.valueOf(q.getOrDefault("kind", "RANDOM")), size);
            int[] data = model.copy();
            if (algos.stream().anyMatch(Algorithm::needsSorted)) Arrays.sort(data);   // same input for both

            int target = 0;
            if (search) {
                String t = q.getOrDefault("target", "").trim();
                target = t.isEmpty() ? data[new Random().nextInt(data.length)] : Integer.parseInt(t);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{\"target\":").append(search ? String.valueOf(target) : "null")
              .append(",\"start\":").append(Arrays.toString(data)).append(",\"runs\":[");
            for (int i = 0; i < algos.size(); i++) {
                if (i > 0) sb.append(',');
                appendRun(sb, algos.get(i), algos.get(i).run(data, target));
            }
            sb.append("]}");
            send(ex, 200, "application/json", sb.toString());
        } catch (IllegalArgumentException e) {      // unknown algorithm or kind, a bad number, or a mixed race
            send(ex, 400, "application/json", "{\"error\":" + str(String.valueOf(e.getMessage())) + "}");
        }
    }

    /** One algorithm's name, complexity, pseudocode and every recorded step. */
    private static void appendRun(StringBuilder sb, Algorithm algo, List<Step> steps) {
        sb.append("{\"name\":").append(str(algo.getName()))
          .append(",\"complexity\":").append(str(algo.complexity()))
          .append(",\"code\":[");
        List<String> code = algo.pseudocode();
        for (int i = 0; i < code.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(str(code.get(i)));
        }
        sb.append("],\"steps\":[");
        for (int i = 0; i < steps.size(); i++) {
            Step s = steps.get(i);
            if (i > 0) sb.append(',');
            StringBuilder roles = new StringBuilder();
            for (Role r : s.getRoles()) roles.append(r.ordinal());       // one digit per element
            sb.append("{\"d\":").append(Arrays.toString(s.getData()))
              .append(",\"r\":\"").append(roles).append('"')
              .append(",\"n\":").append(str(s.getNote()))
              .append(",\"c\":").append(s.getComparisons())
              .append(",\"w\":").append(s.getWrites())
              .append(",\"l\":").append(s.getLine()).append('}');
        }
        sb.append("]}");
    }

    /** Serves web/index.html for every other path. */
    private static void page(HttpExchange ex) throws IOException {
        Path file = Paths.get("web", "index.html");
        if (!Files.exists(file)) {
            send(ex, 404, "text/plain", "web/index.html not found. Run the server from the project folder.");
            return;
        }
        send(ex, 200, "text/html; charset=utf-8", new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
    }

    // ---------- helpers ----------
    private static Map<String, String> query(String raw) {
        Map<String, String> map = new HashMap<>();
        if (raw == null) return map;
        for (String pair : raw.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            map.put(URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
        }
        return map;
    }

    private static String str(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static void send(HttpExchange ex, int code, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }
}
