package visualizer.algorithms.sorting;

import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Picks a pivot, puts smaller values to its left and larger ones to its right (Lomuto partition),
 * then sorts both sides. The pivot rule is a PivotStrategy passed in (composition over inheritance).
 */
public class QuickSort extends SortingAlgorithm {
    private final PivotStrategy pivots;
    private final List<String> code;

    public QuickSort(PivotStrategy pivots) {
        super("Quick Sort (" + pivots.label() + ")", "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)", false);
        this.pivots = pivots;
        this.code = List.of(
                "quickSort(lo, hi):",                               // 1
                "    if lo >= hi: return",                          // 2
                "    " + pivots.rule(),                             // 3  supplied by the strategy
                "    swap a[p], a[hi];  pivot = a[hi];  i = lo",    // 4
                "    for j = lo to hi - 1:",                        // 5
                "        if a[j] < pivot:",                         // 6
                "            swap a[i], a[j];  i = i + 1",          // 7
                "    swap a[i], a[hi]        // pivot in place",    // 8
                "    quickSort(lo, i - 1);  quickSort(i + 1, hi)"); // 9
    }

    @Override
    public List<String> pseudocode() { return code; }

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
        frame(a, "Pivot for a[" + lo + ".." + hi + "] is " + a[p] + " (" + pivots.label() + ")").at(3)
                .range(lo, hi).mark(Role.PIVOT, p).save();
        if (p != hi) {
            swap(a, p, hi);
            frame(a, "Move the pivot " + a[hi] + " to the end").at(4).mark(Role.SWAP, p).mark(Role.PIVOT, hi).save();
        }
        int pivot = a[hi];
        int i = lo;
        for (int j = lo; j < hi; j++) {
            frame(a, "Compare " + a[j] + " with pivot " + pivot).at(6)
                    .mark(Role.SWAP, i).mark(Role.PIVOT, hi).mark(Role.COMPARE, j).save();
            if (less(a[j], pivot)) {
                if (i != j) {
                    swap(a, i, j);
                    frame(a, a[i] + " is smaller than the pivot: move it left").at(7)
                            .mark(Role.PIVOT, hi).mark(Role.SWAP, i, j).save();
                }
                i++;
            }
        }
        if (i != hi) swap(a, i, hi);
        setRole(i, Role.SORTED);                     // the pivot is now in its final place
        frame(a, "Pivot " + pivot + " is now in its final place").at(8).save();
        sort(a, lo, i - 1);
        sort(a, i + 1, hi);
    }
}
