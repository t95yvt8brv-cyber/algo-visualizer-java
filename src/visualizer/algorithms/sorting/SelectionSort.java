package visualizer.algorithms.sorting;

import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/** Finds the smallest remaining value and puts it at the front, one position at a time. */
public class SelectionSort extends SortingAlgorithm {
    private static final List<String> CODE = List.of(
            "for i = 0 to n - 1:",                          // 1
            "    min = i",                                  // 2
            "    for j = i + 1 to n - 1:",                  // 3
            "        if a[j] < a[min]: min = j",            // 4
            "    swap a[i], a[min]");                       // 5

    public SelectionSort() {
        super("Selection Sort", "O(n^2)", "O(n^2)", "O(n^2)", "O(1)", false);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int m = i;                               // index of the smallest value seen so far
            for (int j = i + 1; j < n; j++) {
                frame(a, "Looking for the minimum: compare " + a[j] + " with " + a[m]).at(4)
                        .mark(Role.SWAP, m).mark(Role.PIVOT, i).mark(Role.COMPARE, j).save();
                if (less(a[j], a[m])) m = j;
            }
            if (m != i) {
                swap(a, i, m);
                frame(a, "Put " + a[i] + " at position " + i).at(5).mark(Role.SWAP, i, m).save();
            }
            setRole(i, Role.SORTED);
        }
        finish(a);
    }
}
