package visualizer.algorithms.sorting;

import java.util.List;

/**
 * Least-significant-digit radix sort: one stable counting pass on the ones digit, then the tens,
 * then the hundreds. Because each pass is stable, the earlier digits stay in order.
 * d = number of digits, b = base (10). Works on non-negative values.
 */
public class RadixSort extends DistributionSort {
    private static final List<String> CODE = List.of(
            "max = largest value",                          // 1
            "for exp = 1, 10, 100, ... while max/exp > 0:", // 2
            "    count each digit (a[i] / exp) % 10",       // 3
            "    turn count into end positions",            // 4
            "    place values in out, right to left",       // 5
            "    copy out back into a");                    // 6

    public RadixSort() {
        super("Radix Sort (LSD)", "O(d(n + b))", "O(d(n + b))", "O(d(n + b))", "O(n + b)");
    }

    @Override
    public List<String> pseudocode() { return CODE; }

    @Override
    protected void execute(int[] a, int target) {
        int max = 0;
        for (int v : a) {
            if (v < 0) throw new IllegalArgumentException("Radix sort here needs non-negative values");
            max = Math.max(max, v);
        }
        for (int exp = 1; max / exp > 0; exp *= 10) {
            final int e = exp;
            frame(a, place(exp) + " digit pass").at(2).save();
            countingPass(a, v -> (v / e) % 10, 10, place(exp) + " digit", new Lines(3, 5, 6));
        }
        finish(a);
    }

    private static String place(int exp) {
        switch (exp) {
            case 1:   return "Ones";
            case 10:  return "Tens";
            case 100: return "Hundreds";
            default:  return "10^" + (int) Math.log10(exp);
        }
    }
}
