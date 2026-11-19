package visualizer.algorithms.sorting;

import java.util.Arrays;
import java.util.List;
import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/** Divide and conquer: sort each half, then merge the two sorted halves. */
public class MergeSort extends SortingAlgorithm {
    private static final List<String> CODE = List.of(
            "mergeSort(lo, hi):",                               // 1
            "    if lo >= hi: return",                          // 2
            "    mid = (lo + hi) / 2",                          // 3
            "    mergeSort(lo, mid);  mergeSort(mid + 1, hi)",  // 4
            "    L = a[lo..mid];  R = a[mid+1..hi]",            // 5
            "    while L and R both have values left:",         // 6
            "        if R[j] < L[i]: a[k++] = R[j++]",          // 7
            "        else:           a[k++] = L[i++]",          // 8
            "    copy what is left of L or R into a");          // 9

    public MergeSort() {
        super("Merge Sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(n)", true);
    }

    @Override
    public List<String> pseudocode() { return CODE; }

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

        frame(a, "Merge a[" + lo + ".." + mid + "] with a[" + (mid + 1) + ".." + hi + "]").at(5).range(lo, hi).save();
        int[] left = Arrays.copyOfRange(a, lo, mid + 1);
        int[] right = Arrays.copyOfRange(a, mid + 1, hi + 1);
        int i = 0, j = 0, k = lo;
        while (i < left.length && j < right.length) {
            frame(a, "Merge: compare " + left[i] + " and " + right[j]).at(6).range(lo, hi).mark(Role.COMPARE, k).save();
            int line;
            if (less(right[j], left[i])) {
                write(a, k, right[j++]);
                line = 7;
            } else {
                write(a, k, left[i++]);                        // ties take the left value: stable
                line = 8;
            }
            frame(a, "Write " + a[k] + " to position " + k).at(line).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
        while (i < left.length) {                              // copy what is left of either half
            write(a, k, left[i++]);
            frame(a, "Copy " + a[k] + " to position " + k).at(9).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
        while (j < right.length) {
            write(a, k, right[j++]);
            frame(a, "Copy " + a[k] + " to position " + k).at(9).range(lo, hi).mark(Role.SWAP, k).save();
            k++;
        }
    }
}
