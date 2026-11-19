package visualizer.algorithms;

import visualizer.model.Role;

/** Parent of all searches. Overriding needsTarget() lets the GUI enable the target box (polymorphism). */
public abstract class SearchingAlgorithm extends Algorithm {

    protected SearchingAlgorithm(String name, String best, String average, String worst,
                                 String space, boolean needsSorted) {
        super(name, best, average, worst, space, needsSorted);
    }

    @Override
    public boolean needsTarget() { return true; }

    /** Compares one value with the target, counted as one comparison. Negative, zero or positive, like compareTo. */
    protected final int compare(int value, int target) {
        countComparison();
        return Integer.compare(value, target);
    }

    protected final void found(int[] a, int index, int target) {
        frame(a, "Found " + target + " at index " + index).mark(Role.FOUND, index).save();
    }

    protected final void notFound(int[] a, int target) {
        frame(a, target + " is not in the array").save();
    }

    /** Binary search of a[lo..hi]. Shared by BinarySearch and ExponentialSearch (code reuse). */
    protected final void binarySearch(int[] a, int lo, int hi, int target) {
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = compare(a[mid], target);
            frame(a, "Middle a[" + mid + "] = " + a[mid] + ", target " + target)
                    .range(lo, hi).mark(Role.PIVOT, mid).save();
            if (c == 0) {
                found(a, mid, target);
                return;
            }
            if (c < 0) lo = mid + 1;                 // discard the left half
            else hi = mid - 1;                       // discard the right half
        }
        notFound(a, target);
    }
}
