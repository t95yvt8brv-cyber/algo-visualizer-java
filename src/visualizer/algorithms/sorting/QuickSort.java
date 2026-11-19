package visualizer.algorithms.sorting;

import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Picks a pivot, puts smaller values to its left and larger ones to its right (Lomuto partition),
 * then sorts both sides. The pivot rule is a PivotStrategy passed in (composition over inheritance).
 */
public class QuickSort extends SortingAlgorithm {
    private final PivotStrategy pivots;

    public QuickSort(PivotStrategy pivots) {
        super("Quick Sort (" + pivots.label() + ")", "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)", false);
        this.pivots = pivots;
    }

    @Override
    protected void execute(int[] a, int target) {
        sort(a, 0, a.length - 1);
        finish(a);
    }

    private void sort(int[] a, int lo, int hi) {
        if (lo > hi) return;
        if (lo == hi) {                              // a single element is already in place
            setRole(lo, Role.SORTED);
            return;
        }
        int p = pivots.choose(a, lo, hi, this::less);
        if (p != hi) {
            swap(a, p, hi);
            frame(a, "Chosen pivot " + a[hi] + ", move it to the end").mark(Role.SWAP, p).mark(Role.PIVOT, hi).save();
        }
        int pivot = a[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            frame(a, "Compare " + a[j] + " with pivot " + pivot)
                    .mark(Role.SWAP, i).mark(Role.PIVOT, hi).mark(Role.COMPARE, j).save();
            if (less(a[j], pivot)) {
                if (i != j) {
                    swap(a, i, j);
                    frame(a, "Swap").mark(Role.PIVOT, hi).mark(Role.SWAP, i, j).save();
                }
                i++;
            }
        }
        if (i != hi) swap(a, i, hi);
        setRole(i, Role.SORTED);                     // the pivot is now in its final place
        frame(a, "Pivot " + pivot + " is now in its final place").save();
        sort(a, lo, i - 1);
        sort(a, i + 1, hi);
    }
}
