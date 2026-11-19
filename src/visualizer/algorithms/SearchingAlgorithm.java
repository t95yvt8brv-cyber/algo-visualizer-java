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

    protected final void found(int[] a, int index, int target, int line) {
        frame(a, "Found " + target + " at index " + index).at(line).mark(Role.FOUND, index).save();
    }

    protected final void notFound(int[] a, int target, int line) {
        frame(a, target + " is not in the array").at(line).save();
    }

    /**
     * Binary search of a[lo..hi]. Shared by BinarySearch and ExponentialSearch (code reuse).
     * Its six pseudocode lines start at firstLine:
     *   while lo <= hi / mid = ... / if equal: return / if less: go right / else: go left / not found
     */
    protected final void binarySearch(int[] a, int lo, int hi, int target, int firstLine) {
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = compare(a[mid], target);
            frame(a, "Middle a[" + mid + "] = " + a[mid] + ", target " + target).at(firstLine + 1)
                    .range(lo, hi).mark(Role.PIVOT, mid).save();
            if (c == 0) {
                found(a, mid, target, firstLine + 2);
                return;
            }
            if (c < 0) {
                lo = mid + 1;
                frame(a, a[mid] + " < " + target + ": keep the right half").at(firstLine + 3).range(lo, hi).save();
            } else {
                hi = mid - 1;
                frame(a, a[mid] + " > " + target + ": keep the left half").at(firstLine + 4).range(lo, hi).save();
            }
        }
        notFound(a, target, firstLine + 5);
    }
}
