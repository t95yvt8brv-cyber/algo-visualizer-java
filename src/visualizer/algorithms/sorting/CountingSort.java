package visualizer.algorithms.sorting;

/** Counts how many times each value appears, then writes the values back in order. k = max - min + 1. */
public class CountingSort extends DistributionSort {

    public CountingSort() {
        super("Counting Sort", "O(n + k)", "O(n + k)", "O(n + k)", "O(n + k)");
    }

    @Override
    protected void execute(int[] a, int target) {
        if (a.length > 0) {
            int min = a[0], max = a[0];
            for (int v : a) {
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
            final int offset = min;
            countingPass(a, v -> v - offset, max - min + 1, "Count");
        }
        finish(a);
    }
}
