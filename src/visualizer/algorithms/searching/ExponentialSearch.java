package visualizer.algorithms.searching;

import java.util.List;
import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Checks index 1, 2, 4, 8, ... until it passes the target, then runs binary search on that
 * last range. Good when the target is near the start or the array's length is unknown.
 */
public class ExponentialSearch extends SearchingAlgorithm {
    private static final List<String> CODE = List.of(
            "if a[0] == target: return 0",                  // 1
            "bound = 1",                                    // 2
            "while bound < n and a[bound] < target:",       // 3
            "    bound = bound * 2",                        // 4
            "lo = bound / 2;  hi = min(bound, n - 1)",      // 5
            "while lo <= hi:",                              // 6  shared binary search block, lines 6-11
            "    mid = (lo + hi) / 2",                      // 7
            "    if a[mid] == target: return mid",          // 8
            "    if a[mid] < target: lo = mid + 1",         // 9
            "    else: hi = mid - 1",                       // 10
            "return \"not found\"");                        // 11

    public ExponentialSearch() {
        super("Exponential Search", "O(1)", "O(log i)", "O(log n)", "O(1)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        if (n == 0) {
            notFound(a, target, 11);
            return;
        }
        int c = compare(a[0], target);
        frame(a, "Check a[0] = " + a[0]).at(1).mark(Role.PIVOT, 0).save();
        if (c == 0) {
            found(a, 0, target, 1);
            return;
        }
        int bound = 1;
        while (bound < n) {
            c = compare(a[bound], target);
            frame(a, "Double the bound: a[" + bound + "] = " + a[bound]).at(3)
                    .range(bound / 2, bound).mark(Role.PIVOT, bound).save();
            if (c >= 0) break;
            bound *= 2;
        }
        int lo = bound / 2, hi = Math.min(bound, n - 1);
        frame(a, "Binary search inside a[" + lo + ".." + hi + "]").at(5).range(lo, hi).save();
        binarySearch(a, lo, hi, target, 6);                   // reuse the parent's binary search
    }
}
