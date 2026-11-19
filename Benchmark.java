import java.util.Random;

/**
 * Extension: counts comparisons for every sorting algorithm without the GUI.
 * Counting operations (not seconds) gives the same answer on every machine.
 * Run with:  java Benchmark
 */
public class Benchmark {
    public static void main(String[] args) {
        int[] sizes = {100, 200, 400, 800};
        Random random = new Random(42);
        System.out.printf("%-16s", "Algorithm");
        for (int n : sizes) System.out.printf("%10s", "n=" + n);
        System.out.println("   (comparisons on random data)");

        for (String name : AlgorithmFactory.names()) {
            if (AlgorithmFactory.create(name).needsTarget()) continue;     // sorts only
            System.out.printf("%-16s", name);
            for (int n : sizes) {
                int[] data = new int[n];
                for (int i = 0; i < n; i++) data[i] = random.nextInt(10 * n);
                Algorithm algo = AlgorithmFactory.create(name).withoutRecording();
                algo.run(data, 0);
                System.out.printf("%10d", algo.getComparisons());
            }
            System.out.println();
        }
        System.out.println("Doubling n roughly quadruples O(n^2) sorts but only a little more than doubles O(n log n) sorts.");
    }
}
