package visualizer.algorithms.searching;

import java.util.List;
import visualizer.algorithms.SearchingAlgorithm;

/** Looks at the middle of the range and throws away the half that cannot hold the target. */
public class BinarySearch extends SearchingAlgorithm {
    private static final List<String> CODE = List.of(
            "lo = 0;  hi = n - 1",                          // 1
            "while lo <= hi:",                              // 2  shared binary search block, lines 2-7
            "    mid = (lo + hi) / 2",                      // 3
            "    if a[mid] == target: return mid",          // 4
            "    if a[mid] < target: lo = mid + 1",         // 5
            "    else: hi = mid - 1",                       // 6
            "return \"not found\"");                        // 7

    public BinarySearch() {
        super("Binary Search", "O(1)", "O(log n)", "O(log n)", "O(1)", true);   // needs a sorted array
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        binarySearch(a, 0, a.length - 1, target, 2);
    }
}
