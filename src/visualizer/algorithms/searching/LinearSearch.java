package visualizer.algorithms.searching;

import java.util.List;
import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/** Checks every element from left to right. Works on unsorted data. */
public class LinearSearch extends SearchingAlgorithm {
    private static final List<String> CODE = List.of(
            "for i = 0 to n - 1:",                          // 1
            "    if a[i] == target: return i",              // 2
            "return \"not found\"");                        // 3

    public LinearSearch() {
        super("Linear Search", "O(1)", "O(n)", "O(n)", "O(1)", false);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        for (int i = 0; i < a.length; i++) {
            int c = compare(a[i], target);
            frame(a, "Is a[" + i + "] = " + a[i] + " equal to " + target + "?").at(2).mark(Role.COMPARE, i).save();
            if (c == 0) {
                found(a, i, target, 2);
                return;
            }
        }
        notFound(a, target, 3);
    }
}
