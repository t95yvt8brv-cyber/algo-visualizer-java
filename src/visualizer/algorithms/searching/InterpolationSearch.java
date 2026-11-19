package visualizer.algorithms.searching;

import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Like looking up a word in a dictionary: guesses the position from the value
 * (a target near the top of the range is probably near the end). Very fast on evenly
 * spread values, O(n) when the values are bunched up.
 */
public class InterpolationSearch extends SearchingAlgorithm {

    public InterpolationSearch() {
        super("Interpolation Search", "O(1)", "O(log log n)", "O(n)", "O(1)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            if (compare(a[lo], target) > 0 || compare(a[hi], target) < 0) break;   // outside the range
            int pos = a[hi] == a[lo]
                    ? lo
                    : lo + (int) ((long) (target - a[lo]) * (hi - lo) / (a[hi] - a[lo]));
            int c = compare(a[pos], target);
            frame(a, "Estimate from the values: position " + pos + " (a[" + pos + "] = " + a[pos] + ")")
                    .range(lo, hi).mark(Role.PIVOT, pos).save();
            if (c == 0) {
                found(a, pos, target);
                return;
            }
            if (c < 0) lo = pos + 1;
            else hi = pos - 1;
        }
        notFound(a, target);
    }
}
