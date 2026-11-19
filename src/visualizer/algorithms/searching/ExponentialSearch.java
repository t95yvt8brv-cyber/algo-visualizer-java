package visualizer.algorithms.searching;

import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Checks index 1, 2, 4, 8, ... until it passes the target, then runs binary search on that
 * last range. Good when the target is near the start or the array's length is unknown.
 */
public class ExponentialSearch extends SearchingAlgorithm {

    public ExponentialSearch() {
        super("Exponential Search", "O(1)", "O(log i)", "O(log n)", "O(1)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        if (n == 0) {
            notFound(a, target);
            return;
        }
        int c = compare(a[0], target);
        frame(a, "Check a[0] = " + a[0]).mark(Role.PIVOT, 0).save();
        if (c == 0) {
            found(a, 0, target);
            return;
        }
        int bound = 1;
        while (bound < n) {
            c = compare(a[bound], target);
            frame(a, "Double the bound: a[" + bound + "] = " + a[bound])
                    .range(bound / 2, bound).mark(Role.PIVOT, bound).save();
            if (c >= 0) break;
            bound *= 2;
        }
        binarySearch(a, bound / 2, Math.min(bound, n - 1), target);   // reuse the parent's binary search
    }
}
