package visualizer.algorithms.sorting;

/** The median of the first, middle and last elements. Sorted and reversed input stay O(n log n). */
public class MedianOfThreePivot implements PivotStrategy {

    @Override
    public int choose(int[] a, int lo, int hi, Less less) {
        int x = lo, y = (lo + hi) >>> 1, z = hi;
        if (less.test(a[y], a[x])) { int t = x; x = y; y = t; }     // now a[x] <= a[y]
        if (less.test(a[z], a[y])) {
            y = z;                                                    // a[z] is below the old middle
            if (less.test(a[y], a[x])) y = x;
        }
        return y;
    }

    @Override public String label() { return "median of 3"; }
}
