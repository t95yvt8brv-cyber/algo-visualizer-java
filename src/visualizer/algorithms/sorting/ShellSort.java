package visualizer.algorithms.sorting;

import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Insertion sort on elements that are `gap` apart, with the gap shrinking to 1.
 * Far-apart swaps move values close to their place early, so the final gap-1 pass is quick.
 * Uses Knuth's gap sequence 1, 4, 13, 40, ...
 */
public class ShellSort extends SortingAlgorithm {

    public ShellSort() {
        super("Shell Sort", "O(n log n)", "~O(n^1.25)", "O(n^1.5)", "O(1)", false);
    }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length, gap = 1;
        while (gap < n / 3) gap = 3 * gap + 1;
        for (; gap >= 1; gap /= 3) {
            frame(a, "Gap " + gap + ": insertion sort on elements " + gap + " apart").save();
            for (int i = gap; i < n; i++) {
                for (int j = i; j >= gap; j -= gap) {
                    frame(a, "Gap " + gap + ": compare " + a[j - gap] + " and " + a[j])
                            .mark(Role.COMPARE, j - gap, j).save();
                    if (!less(a[j], a[j - gap])) break;
                    swap(a, j, j - gap);
                    frame(a, "Swap across gap " + gap).mark(Role.SWAP, j - gap, j).save();
                }
            }
        }
        finish(a);
    }
}
