package visualizer.algorithms.sorting;

import java.util.Random;

/** A random element. No fixed input is bad every time; expected O(n log n). */
public class RandomPivot implements PivotStrategy {
    private final Random random = new Random();

    @Override public int choose(int[] a, int lo, int hi, Less less) { return lo + random.nextInt(hi - lo + 1); }
    @Override public String label() { return "random pivot"; }
}
