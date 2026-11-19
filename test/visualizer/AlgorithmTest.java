package visualizer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.AlgorithmFactory;
import visualizer.model.ArrayModel;
import visualizer.model.Role;
import visualizer.model.Step;

/** Plain-Java tests (no JUnit needed). Run with:  java -cp out visualizer.AlgorithmTest */
public class AlgorithmTest {
    private static int failures = 0;
    private static int checks = 0;

    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) {
            failures++;
            System.out.println("FAIL: " + message);
        }
    }

    public static void main(String[] args) {
        int sorts = 0, searches = 0;
        for (String name : AlgorithmFactory.names()) {
            if (AlgorithmFactory.create(name).needsTarget()) searches++;
            else sorts++;
        }
        fixedSortCases();
        randomSortCases();
        searchCases();
        countingCases();
        designCases();

        System.out.println(failures == 0
                ? "All " + checks + " checks passed (" + sorts + " sorts, " + searches + " searches)."
                : failures + " of " + checks + " checks failed.");
        if (failures > 0) System.exit(1);
    }

    /** Edge cases: empty, one element, two, all equal, descending. */
    private static void fixedSortCases() {
        int[] descending = new int[20];
        for (int i = 0; i < 20; i++) descending[i] = 20 - i;
        int[][] cases = {{}, {1}, {2, 1}, {5, 5, 5}, {3, 1, 2, 3, 1}, descending};
        for (String name : AlgorithmFactory.names()) {
            if (AlgorithmFactory.create(name).needsTarget()) continue;
            for (int[] c : cases) checkSort(name, c);
        }
    }

    /** 200 arrays of every kind and random size for every sort. */
    private static void randomSortCases() {
        Random random = new Random(1);
        ArrayModel model = new ArrayModel();
        for (int trial = 0; trial < 200; trial++) {
            for (ArrayModel.Kind kind : ArrayModel.Kind.values()) {
                model.generate(kind, 1 + random.nextInt(100));
                for (String name : AlgorithmFactory.names()) {
                    if (!AlgorithmFactory.create(name).needsTarget()) checkSort(name, model.copy());
                }
            }
        }
    }

    private static void checkSort(String name, int[] input) {
        List<Step> steps = AlgorithmFactory.create(name).run(input, 0);
        Step last = steps.get(steps.size() - 1);
        int[] expected = input.clone();
        Arrays.sort(expected);
        check(Arrays.equals(last.getData(), expected), name + " failed on " + Arrays.toString(input));
        for (Role r : last.getRoles()) {
            if (r != Role.SORTED) { check(false, name + ": last frame is not all SORTED"); break; }
        }
        for (Step s : steps) {
            if (s.size() != input.length) { check(false, name + ": a frame has the wrong length"); break; }
        }
    }

    private static void searchCases() {
        int[] sorted = new int[128];
        for (int i = 0; i < 128; i++) sorted[i] = i + 1;
        Random random = new Random(2);
        ArrayModel model = new ArrayModel();
        for (String name : AlgorithmFactory.names()) {
            if (!AlgorithmFactory.create(name).needsTarget()) continue;
            check(foundAt(name, sorted, 8) == 7, name + " did not find 8 at index 7");
            check(foundAt(name, sorted, 1) == 0, name + " did not find the first element");
            check(foundAt(name, sorted, 128) == 127, name + " did not find the last element");
            List<Step> missing = AlgorithmFactory.create(name).run(sorted, 1000);
            check(missing.get(missing.size() - 1).getNote().contains("not in"), name + " should report 'not in the array'");
            check(foundAt(name, new int[0], 5) == -1, name + " failed on an empty array");

            // random arrays of every kind, including many duplicates (few unique)
            for (int trial = 0; trial < 100; trial++) {
                for (ArrayModel.Kind kind : ArrayModel.Kind.values()) {
                    model.generate(kind, 1 + random.nextInt(100));
                    int[] data = model.copy();
                    Arrays.sort(data);                           // every search may run on sorted data
                    int target = random.nextBoolean() ? data[random.nextInt(data.length)] : random.nextInt(130);
                    boolean present = Arrays.stream(data).anyMatch(v -> v == target);
                    int at = foundAt(name, data, target);
                    check(present == (at >= 0) && (at < 0 || data[at] == target),
                            name + " wrong for target " + target + " in " + Arrays.toString(data));
                }
            }
        }
    }

    /** Index the last frame marks FOUND, or -1. */
    private static int foundAt(String name, int[] data, int target) {
        List<Step> steps = AlgorithmFactory.create(name).run(data, target);
        Step last = steps.get(steps.size() - 1);
        for (int i = 0; i < last.size(); i++) if (last.roleAt(i) == Role.FOUND) return i;
        return -1;
    }

    private static void countingCases() {
        int[] sorted = new int[128];
        for (int i = 0; i < 128; i++) sorted[i] = i + 1;
        check(comparisons("Binary Search", sorted, 128) < comparisons("Linear Search", sorted, 128),
                "binary search should use fewer comparisons than linear");
        check(comparisons("Interpolation Search", sorted, 100) <= 3,
                "interpolation search should be almost instant on evenly spread values");

        int[] reversed = new int[300];
        for (int i = 0; i < 300; i++) reversed[i] = 300 - i;
        check(comparisons("Counting Sort", reversed, 0) == 0, "counting sort should make no comparisons");
        check(comparisons("Radix Sort (LSD)", reversed, 0) == 0, "radix sort should make no comparisons");
        check(comparisons("Quick Sort (median of 3)", reversed, 0) * 5 < comparisons("Quick Sort (last pivot)", reversed, 0),
                "median-of-3 pivot should beat the last-element pivot on reversed data");
    }

    private static int comparisons(String name, int[] data, int target) {
        Algorithm algo = AlgorithmFactory.create(name).withoutRecording();
        algo.run(data, target);
        return algo.getComparisons();
    }

    /** The OOP guarantees: immutable steps, clear errors. */
    private static void designCases() {
        List<Step> steps = AlgorithmFactory.create("Bubble Sort").run(new int[] {3, 2, 1}, 0);
        Step first = steps.get(0);
        int before = first.valueAt(0);
        first.getData()[0] = 99;
        first.getRoles()[0] = Role.FOUND;
        check(first.valueAt(0) == before && first.roleAt(0) != Role.FOUND, "Step must not be changeable from outside");

        check(rejects(() -> steps.add(first)), "the list of steps must be read-only");
        check(rejects(() -> AlgorithmFactory.create("No Such Sort")), "unknown names must throw IllegalArgumentException");
        check(rejects(() -> AlgorithmFactory.create("Binary Search").run(new int[] {3, 1, 2}, 1)),
                "binary search on unsorted data must throw IllegalArgumentException");
    }

    /** True if the action is refused with IllegalArgumentException or UnsupportedOperationException. */
    private static boolean rejects(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            return true;
        }
    }
}
