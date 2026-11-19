package visualizer.algorithms.sorting;

import java.util.function.IntUnaryOperator;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Parent of the non-comparison sorts. They never compare two values, they place each value by
 * its key, which is how they get under the O(n log n) limit that comparison sorts can't beat.
 */
public abstract class DistributionSort extends SortingAlgorithm {

    protected DistributionSort(String name, String best, String average, String worst, String space) {
        super(name, best, average, worst, space, true);
    }

    /**
     * One stable counting pass: rearranges `a` by key(value), which must be in 0..buckets-1.
     * Count each key, turn the counts into end positions, place values right to left into a
     * buffer (this keeps equal keys in order), then copy the buffer back.
     */
    protected final void countingPass(int[] a, IntUnaryOperator key, int buckets, String label) {
        int[] count = new int[buckets];
        for (int i = 0; i < a.length; i++) {
            int k = key.applyAsInt(a[i]);
            count[k]++;
            frame(a, label + ": " + a[i] + " goes in bucket " + k).mark(Role.COMPARE, i).save();
        }
        for (int b = 1; b < buckets; b++) count[b] += count[b - 1];
        int[] out = new int[a.length];
        for (int i = a.length - 1; i >= 0; i--) out[--count[key.applyAsInt(a[i])]] = a[i];
        for (int k = 0; k < a.length; k++) {
            write(a, k, out[k]);
            frame(a, label + ": write " + out[k] + " to position " + k).mark(Role.SWAP, k).save();
        }
    }
}
