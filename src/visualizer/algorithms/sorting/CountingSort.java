package visualizer.algorithms.sorting;

import java.util.List;

/** Counts how many times each value appears, then writes the values back in order. k = max - min + 1. */
public class CountingSort extends DistributionSort {
    private static final List<String> CODE = List.of(
            "min, max = smallest and largest value",        // 1
            "count = (max - min + 1) zeros",                // 2
            "for each v in a: count[v - min] += 1",         // 3
            "turn count into end positions (running sum)",  // 4
            "for i = n-1 down to 0: out[--count[a[i]-min]] = a[i]", // 5
            "copy out back into a");                        // 6

    public CountingSort() {
        super("Counting Sort", "O(n + k)", "O(n + k)", "O(n + k)", "O(n + k)");
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        if (a.length > 0) {
            int min = a[0], max = a[0];
            for (int v : a) {
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
            frame(a, "Values go from " + min + " to " + max + ", so " + (max - min + 1) + " buckets").at(2).save();
            final int offset = min;
            countingPass(a, v -> v - offset, max - min + 1, "Count", new Lines(3, 5, 6));
        }
        finish(a);
    }
}
