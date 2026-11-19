package visualizer.algorithms.searching;

import java.util.List;
import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Like looking up a word in a dictionary: guesses the position from the value
 * (a target near the top of the range is probably near the end). Very fast on evenly
 * spread values, O(n) when the values are bunched up.
 */
public class InterpolationSearch extends SearchingAlgorithm {
    private static final List<String> CODE = List.of(
            "lo = 0;  hi = n - 1",                                       // 1
            "while lo <= hi and a[lo] <= target <= a[hi]:",              // 2
            "    pos = lo + (target-a[lo]) * (hi-lo) / (a[hi]-a[lo])",   // 3
            "    if a[pos] == target: return pos",                       // 4
            "    if a[pos] < target: lo = pos + 1",                      // 5
            "    else: hi = pos - 1",                                    // 6
            "return \"not found\"");                                     // 7

    public InterpolationSearch() {
        super("Interpolation Search", "O(1)", "O(log log n)", "O(n)", "O(1)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            if (compare(a[lo], target) > 0 || compare(a[hi], target) < 0) break;   // outside the range
            int pos = a[hi] == a[lo]
                    ? lo
                    : lo + (int) ((long) (target - a[lo]) * (hi - lo) / (a[hi] - a[lo]));
            int c = compare(a[pos], target);
            frame(a, "Estimate from the values: position " + pos + " (a[" + pos + "] = " + a[pos] + ")").at(3)
                    .range(lo, hi).mark(Role.PIVOT, pos).save();
            if (c == 0) {
                found(a, pos, target, 4);
                return;
            }
            if (c < 0) {
                lo = pos + 1;
                frame(a, a[pos] + " < " + target + ": look to the right").at(5).range(lo, hi).save();
            } else {
                hi = pos - 1;
                frame(a, a[pos] + " > " + target + ": look to the left").at(6).range(lo, hi).save();
            }
        }
        notFound(a, target, 7);
    }
}
