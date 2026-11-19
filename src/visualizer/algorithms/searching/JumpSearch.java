package visualizer.algorithms.searching;

import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Jumps ahead in blocks of sqrt(n) until a block's last value is >= the target,
 * then scans that one block. Fewer far jumps than binary search, which helps when jumping back is costly.
 */
public class JumpSearch extends SearchingAlgorithm {

    public JumpSearch() {
        super("Jump Search", "O(1)", "O(√n)", "O(√n)", "O(1)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        if (n == 0) {
            notFound(a, target);
            return;
        }
        int jump = Math.max(1, (int) Math.sqrt(n));
        int lo = 0, hi = Math.min(jump, n) - 1;
        while (true) {                                         // find the block
            int c = compare(a[hi], target);
            frame(a, "Jump: last value of block a[" + lo + ".." + hi + "] is " + a[hi])
                    .range(lo, hi).mark(Role.PIVOT, hi).save();
            if (c >= 0) break;                                 // the target can only be in this block
            lo = hi + 1;
            if (lo >= n) {
                notFound(a, target);
                return;
            }
            hi = Math.min(hi + jump, n - 1);
        }
        for (int i = lo; i <= hi; i++) {                       // scan the block
            int c = compare(a[i], target);
            frame(a, "Scan block: is a[" + i + "] = " + a[i] + " equal to " + target + "?")
                    .range(lo, hi).mark(Role.COMPARE, i).save();
            if (c == 0) {
                found(a, i, target);
                return;
            }
            if (c > 0) break;                                  // passed where it would be
        }
        notFound(a, target);
    }
}
