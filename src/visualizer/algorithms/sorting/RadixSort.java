package visualizer.algorithms.sorting;

/**
 * Least-significant-digit radix sort: one stable counting pass on the ones digit, then the tens,
 * then the hundreds. Because each pass is stable, the earlier digits stay in order.
 * d = number of digits, b = base (10). Works on non-negative values.
 */
public class RadixSort extends DistributionSort {

    public RadixSort() {
        super("Radix Sort (LSD)", "O(d(n + b))", "O(d(n + b))", "O(d(n + b))", "O(n + b)");
    }

    @Override
    protected void execute(int[] a, int target) {
        int max = 0;
        for (int v : a) {
            if (v < 0) throw new IllegalArgumentException("Radix sort here needs non-negative values");
            max = Math.max(max, v);
        }
        for (int exp = 1; max / exp > 0; exp *= 10) {
            final int e = exp;
            countingPass(a, v -> (v / e) % 10, 10, place(exp) + " digit");
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
