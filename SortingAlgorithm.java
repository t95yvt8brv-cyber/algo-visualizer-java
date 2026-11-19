import java.util.Arrays;

/** Parent of all sorts: shares the counting helpers (inheritance). */
public abstract class SortingAlgorithm extends Algorithm {

    protected SortingAlgorithm(String name, String best, String average, String worst, String space) {
        super(name, best, average, worst, space, false);
    }

    /** x < y, counted as one comparison. */
    protected boolean less(int x, int y) {
        comparisons++;
        return x < y;
    }

    /** Swaps two elements, counted as two writes. */
    protected void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        writes += 2;
    }

    /** Marks everything sorted and records the final frame. */
    protected void finish(int[] a) {
        Arrays.fill(roles, Role.SORTED);
        frame(a, "Sorted!").save();
    }
}
