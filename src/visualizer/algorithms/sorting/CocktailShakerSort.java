package visualizer.algorithms.sorting;

import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Bubble sort in both directions: a forward pass carries the largest value right,
 * then a backward pass carries the smallest value left. Small values near the end
 * ("turtles") move much faster than in plain bubble sort.
 */
public class CocktailShakerSort extends SortingAlgorithm {
    private static final List<String> CODE = List.of(
            "lo = 0;  hi = n - 1",                                  // 1
            "while lo < hi:",                                       // 2
            "    for j = lo to hi - 1:              // forward",    // 3
            "        if a[j + 1] < a[j]: swap them",                // 4
            "    hi = hi - 1",                                      // 5
            "    for j = hi down to lo + 1:         // backward",   // 6
            "        if a[j] < a[j - 1]: swap them",                // 7
            "    lo = lo + 1",                                      // 8
            "    if a pass made no swaps: stop");                   // 9

    public CocktailShakerSort() {
        super("Cocktail Shaker Sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            boolean swapped = false;
            for (int j = lo; j < hi; j++) {
                frame(a, "Forward: compare " + a[j] + " and " + a[j + 1]).at(4).mark(Role.COMPARE, j, j + 1).save();
                if (less(a[j + 1], a[j])) {
                    swap(a, j, j + 1);
                    swapped = true;
                    frame(a, "Swap").at(4).mark(Role.SWAP, j, j + 1).save();
                }
            }
            setRole(hi--, Role.SORTED);
            if (!swapped) {
                frame(a, "No swaps going forward, so the array is sorted").at(9).save();
                break;
            }

            swapped = false;
            for (int j = hi; j > lo; j--) {
                frame(a, "Backward: compare " + a[j - 1] + " and " + a[j]).at(7).mark(Role.COMPARE, j - 1, j).save();
                if (less(a[j], a[j - 1])) {
                    swap(a, j - 1, j);
                    swapped = true;
                    frame(a, "Swap").at(7).mark(Role.SWAP, j - 1, j).save();
                }
            }
            setRole(lo++, Role.SORTED);
            if (!swapped) {
                frame(a, "No swaps going backward, so the array is sorted").at(9).save();
                break;
            }
        }
        finish(a);
    }
}
