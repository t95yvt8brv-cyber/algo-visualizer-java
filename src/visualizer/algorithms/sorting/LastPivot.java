package visualizer.algorithms.sorting;

/** Always the last element. Simple, but sorted or reversed input makes Quick Sort O(n^2). */
public class LastPivot implements PivotStrategy {
    @Override public int choose(int[] a, int lo, int hi, Less less) { return hi; }
    @Override public String label() { return "last pivot"; }
    @Override public String rule() { return "p = hi"; }
}
