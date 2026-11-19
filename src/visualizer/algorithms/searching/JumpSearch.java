package visualizer.algorithms.searching;

import java.util.List;
import visualizer.algorithms.SearchingAlgorithm;
import visualizer.model.Role;

/**
 * Jumps ahead in blocks of sqrt(n) until a block's last value is >= the target,
 * then scans that one block. Fewer far jumps than binary search, which helps when jumping back is costly.
 */
public class JumpSearch extends SearchingAlgorithm {
    private static final List<String> CODE = List.of(
            "step = sqrt(n);  lo = 0;  hi = step - 1",      // 1
            "while a[hi] < target:          // find block", // 2
            "    lo = hi + 1;  hi = min(hi + step, n - 1)", // 3
            "    if lo >= n: return \"not found\"",         // 4
            "for i = lo to hi:              // scan block", // 5
            "    if a[i] == target: return i",              // 6
            "return \"not found\"");                        // 7

    public JumpSearch() {
        super("Jump Search", "O(1)", "O(√n)", "O(√n)", "O(1)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        if (n == 0) {
            notFound(a, target, 7);
            return;
        }
        int jump = Math.max(1, (int) Math.sqrt(n));
        int lo = 0, hi = Math.min(jump, n) - 1;
        while (true) {
            int c = compare(a[hi], target);
            frame(a, "Jump: last value of block a[" + lo + ".." + hi + "] is " + a[hi]).at(2)
                    .range(lo, hi).mark(Role.PIVOT, hi).save();
            if (c >= 0) break;                                 // the target can only be in this block
            lo = hi + 1;
            if (lo >= n) {
                notFound(a, target, 4);
                return;
            }
            hi = Math.min(hi + jump, n - 1);
        }
        for (int i = lo; i <= hi; i++) {
            int c = compare(a[i], target);
            frame(a, "Scan block: is a[" + i + "] = " + a[i] + " equal to " + target + "?").at(6)
                    .range(lo, hi).mark(Role.COMPARE, i).save();
            if (c == 0) {
                found(a, i, target, 6);
                return;
            }
            if (c > 0) break;                                  // passed where it would be
        }
        notFound(a, target, 7);
    }
}
