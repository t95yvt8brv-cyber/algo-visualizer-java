package visualizer.algorithms.sorting;

/**
 * How Quick Sort picks its pivot (Strategy pattern). QuickSort works with any implementation,
 * so the pivot rule can change without touching QuickSort itself.
 */
public interface PivotStrategy {

    /** A counted "x < y" supplied by the sort, so pivot comparisons show up in the statistics. */
    @FunctionalInterface
    interface Less {
        boolean test(int x, int y);
    }

    /** Returns the index (between lo and hi) of the value to use as the pivot. */
    int choose(int[] a, int lo, int hi, Less less);

    /** Short label shown in the algorithm's name. */
    String label();
}
