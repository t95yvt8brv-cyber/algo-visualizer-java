package visualizer.algorithms.searching;

import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/** Checks every element from left to right. Works on unsorted data. */
public class LinearSearch extends SearchingAlgorithm {

    public LinearSearch() {
        super("Linear Search", "O(1)", "O(n)", "O(n)", "O(1)", false);
    }

    @Override
    protected void execute(int[] a, int target) {
        for (int i = 0; i < a.length; i++) {
            int c = compare(a[i], target);
            frame(a, "Is a[" + i + "] = " + a[i] + " equal to " + target + "?").mark(Role.COMPARE, i).save();
            if (c == 0) {
                found(a, i, target);
                return;
            }
        }
        notFound(a, target);
    }
}
