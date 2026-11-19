package visualizer.algorithms.sorting;

import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Bubble sort in both directions: a forward pass carries the largest value right,
 * then a backward pass carries the smallest value left. Small values near the end
 * ("turtles") move much faster than in plain bubble sort.
 */
public class CocktailShakerSort extends SortingAlgorithm {

    public CocktailShakerSort() {
        super("Cocktail Shaker Sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo < hi) {
            boolean swapped = false;
            for (int j = lo; j < hi; j++) {                    // forward pass
                frame(a, "Forward: compare " + a[j] + " and " + a[j + 1]).mark(Role.COMPARE, j, j + 1).save();
                if (less(a[j + 1], a[j])) {
                    swap(a, j, j + 1);
                    swapped = true;
                    frame(a, "Swap").mark(Role.SWAP, j, j + 1).save();
                }
            }
            setRole(hi--, Role.SORTED);
            if (!swapped) break;

            swapped = false;
            for (int j = hi; j > lo; j--) {                    // backward pass
                frame(a, "Backward: compare " + a[j - 1] + " and " + a[j]).mark(Role.COMPARE, j - 1, j).save();
                if (less(a[j], a[j - 1])) {
                    swap(a, j - 1, j);
                    swapped = true;
                    frame(a, "Swap").mark(Role.SWAP, j - 1, j).save();
                }
            }
            setRole(lo++, Role.SORTED);
            if (!swapped) break;
        }
        finish(a);
    }
}
