package visualizer.algorithms.sorting;

import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Insertion sort on elements that are `gap` apart, with the gap shrinking to 1.
 * Far-apart swaps move values close to their place early, so the final gap-1 pass is quick.
 * Uses Knuth's gap sequence 1, 4, 13, 40, ...
 */
public class ShellSort extends SortingAlgorithm {
    private static final List<String> CODE = List.of(
            "gap = largest of 1, 4, 13, 40, ... below n/3",  // 1
            "while gap >= 1:",                              // 2
            "    for i = gap to n - 1:",                    // 3
            "        j = i",                                // 4
            "        while j >= gap and a[j] < a[j - gap]:",// 5
            "            swap a[j], a[j - gap]",            // 6
            "            j = j - gap",                      // 7
            "    gap = gap / 3");                           // 8

    public ShellSort() {
        super("Shell Sort", "O(n log n)", "~O(n^1.25)", "O(n^1.5)", "O(1)", false);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length, gap = 1;
        while (gap < n / 3) gap = 3 * gap + 1;
        for (; gap >= 1; gap /= 3) {
            frame(a, "Gap " + gap + ": insertion sort on elements " + gap + " apart").at(2).save();
            for (int i = gap; i < n; i++) {
                for (int j = i; j >= gap; j -= gap) {
                    frame(a, "Gap " + gap + ": compare " + a[j - gap] + " and " + a[j]).at(5)
                            .mark(Role.COMPARE, j - gap, j).save();
                    if (!less(a[j], a[j - gap])) break;
                    swap(a, j, j - gap);
                    frame(a, "Swap across gap " + gap).at(6).mark(Role.SWAP, j - gap, j).save();
                }
            }
        }
        finish(a);
    }
}
