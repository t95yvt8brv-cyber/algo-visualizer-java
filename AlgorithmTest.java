import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Plain-Java tests (no JUnit needed). Run with:  java AlgorithmTest */
public class AlgorithmTest {
    private static int failures = 0;

    private static void check(boolean ok, String message) {
        if (!ok) {
            failures++;
            System.out.println("FAIL: " + message);
        }
    }

    public static void main(String[] args) {
        Random random = new Random(1);
        int[] descending = new int[20];
        for (int i = 0; i < 20; i++) descending[i] = 20 - i;
        int[] randomArray = new int[60];
        for (int i = 0; i < 60; i++) randomArray[i] = random.nextInt(50);
        int[][] cases = {{}, {1}, {2, 1}, {5, 5, 5}, descending, randomArray};

        int sortsTested = 0;
        for (String name : AlgorithmFactory.names()) {
            Algorithm algo = AlgorithmFactory.create(name);
            if (algo.needsTarget()) continue;
            sortsTested++;
            for (int[] c : cases) {
                List<Step> steps = AlgorithmFactory.create(name).run(c, 0);
                int[] result = steps.get(steps.size() - 1).getData();
                int[] expected = c.clone();
                Arrays.sort(expected);
                check(Arrays.equals(result, expected), name + " failed on " + Arrays.toString(c));
            }
        }

        int[] sorted = new int[128];
        for (int i = 0; i < 128; i++) sorted[i] = i + 1;
        for (String name : AlgorithmFactory.names()) {
            Algorithm algo = AlgorithmFactory.create(name);
            if (!algo.needsTarget()) continue;
            List<Step> found = algo.run(sorted, 8);
            check(found.get(found.size() - 1).getRoles()[7] == Role.FOUND, name + " did not find 8 at index 7");
            List<Step> missing = AlgorithmFactory.create(name).run(sorted, 1000);
            check(missing.get(missing.size() - 1).getNote().contains("not in"),
                    name + " should report 'not in the array'");
        }

        Algorithm linear = AlgorithmFactory.create("Linear Search");
        Algorithm binary = AlgorithmFactory.create("Binary Search");
        linear.run(sorted, 128);
        binary.run(sorted, 128);
        check(binary.getComparisons() < linear.getComparisons(), "binary search should use fewer comparisons");

        System.out.println(failures == 0
                ? "All tests passed (" + sortsTested + " sorting algorithms, 2 searches)."
                : failures + " test(s) failed.");
        if (failures > 0) System.exit(1);
    }
}
