package visualizer.algorithms.sorting;

import java.util.Arrays;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/** Divide and conquer: sort each half, then merge the two sorted halves. */
public class MergeSort extends SortingAlgorithm {

    public MergeSort() {
        super("Merge Sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", true);
    }

    @Override
    protected void execute(int[] a, int target) {
        sort(a, 0, a.length - 1);
        finish(a);
    }

    private void sort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        sort(a, lo, mid);
        sort(a, mid + 1, hi);

        int[] left = Arrays.copyOfRange(a, lo, mid + 1);
        int[] right = Arrays.copyOfRange(a, mid + 1, hi + 1);
        int i = 0, j = 0, k = lo;
        while (i < left.length && j < right.length) {
            frame(a, "Merge: compare " + left[i] + " and " + right[j]).range(lo, hi).mark(Role.COMPARE, k).save();
            if (less(right[j], left[i])) write(a, k, right[j++]);
            else write(a, k, left[i++]);                       // ties take the left value: stable
            frame(a, "Write " + a[k] + " to position " + k).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
        while (i < left.length) {                              // copy what is left of either half
            write(a, k, left[i++]);
            frame(a, "Copy " + a[k] + " to position " + k).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
        while (j < right.length) {
            write(a, k, right[j++]);
            frame(a, "Copy " + a[k] + " to position " + k).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
    }
}
