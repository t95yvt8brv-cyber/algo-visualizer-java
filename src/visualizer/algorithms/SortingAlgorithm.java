package visualizer.algorithms;

import visualizer.model.Role;

/** Parent of all sorts: shares the counting helpers (inheritance). */
public abstract class SortingAlgorithm extends Algorithm {
    private final boolean stable;

    protected SortingAlgorithm(String name, String best, String average, String worst,
                               String space, boolean stable) {
        super(name, best, average, worst, space, false);
        this.stable = stable;
    }

    /** A stable sort keeps equal values in their original order. */
    public boolean isStable() { return stable; }

    @Override
    public String complexity() {                               // extends the parent's text (overriding)
        return super.complexity() + " | Stable " + (stable ? "yes" : "no");
    }

    /** x < y, counted as one comparison. */
    protected final boolean less(int x, int y) {
        countComparison();
        return x < y;
    }

    /** Swaps two elements, counted as two writes. */
    protected final void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        countWrites(2);
    }

    /** Stores one value, counted as one write. */
    protected final void write(int[] a, int k, int value) {
        a[k] = value;
        countWrites(1);
    }

    /** Marks everything sorted and records the final frame. */
    protected final void finish(int[] a) {
        setAllRoles(Role.SORTED);
        frame(a, "Sorted!").save();
    }
}
