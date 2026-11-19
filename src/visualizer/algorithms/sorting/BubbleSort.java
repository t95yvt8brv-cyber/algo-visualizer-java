package visualizer.algorithms.sorting;

import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/** Repeatedly swaps neighbours that are out of order; the largest value bubbles to the end each pass. */
public class BubbleSort extends SortingAlgorithm {
    private static final List<String> CODE = List.of(
            "for i = 0 to n - 2:",                          // 1
            "    swapped = false",                          // 2
            "    for j = 0 to n - 2 - i:",                  // 3
            "        if a[j + 1] < a[j]:",                  // 4
            "            swap a[j], a[j + 1]",              // 5
            "            swapped = true",                   // 6
            "    if not swapped: stop");                    // 7

    public BubbleSort() {
        super("Bubble Sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                frame(a, "Compare " + a[j] + " and " + a[j + 1]).at(4).mark(Role.COMPARE, j, j + 1).save();
                if (less(a[j + 1], a[j])) {
                    swap(a, j, j + 1);
                    swapped = true;
                    frame(a, "Swap").at(5).mark(Role.SWAP, j, j + 1).save();
                }
            }
            setRole(n - 1 - i, Role.SORTED);         // the largest value has bubbled to the end
            if (!swapped) {                          // no swaps in a whole pass: already sorted
                frame(a, "No swaps in this pass, so the array is sorted").at(7).save();
                break;
            }
        }
        finish(a);
    }
}
