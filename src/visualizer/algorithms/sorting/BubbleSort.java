package visualizer.algorithms.sorting;

import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/** Repeatedly swaps neighbours that are out of order; the largest value bubbles to the end each pass. */
public class BubbleSort extends SortingAlgorithm {

    public BubbleSort() {
        super("Bubble Sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                frame(a, "Compare " + a[j] + " and " + a[j + 1]).mark(Role.COMPARE, j, j + 1).save();
                if (less(a[j + 1], a[j])) {
                    swap(a, j, j + 1);
                    swapped = true;
                    frame(a, "Swap").mark(Role.SWAP, j, j + 1).save();
                }
            }
            setRole(n - 1 - i, Role.SORTED);         // the largest value has bubbled to the end
            if (!swapped) break;                     // no swaps in a whole pass: already sorted
        }
        finish(a);
    }
}
