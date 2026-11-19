package visualizer.algorithms.sorting;

import visualizer.algorithms.SortingAlgorithm;
import visualizer.model.Role;

/**
 * Builds a max-heap inside the array (each parent >= its children), then repeatedly moves the
 * root (the maximum) to the end and repairs the heap. In place and always O(n log n).
 */
public class HeapSort extends SortingAlgorithm {

    public HeapSort() {
        super("Heap Sort", "O(n log n)", "O(n log n)", "O(n log n)", "O(1)", false);
    }

    @Override
    protected void execute(int[] a, int target) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) siftDown(a, i, n, "Build heap");
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end);
            setRole(end, Role.SORTED);
            frame(a, "Move the maximum " + a[end] + " to position " + end).mark(Role.SWAP, 0, end).save();
            siftDown(a, 0, end, "Repair heap");
        }
        finish(a);
    }

    /** Moves a[i] down until it is larger than both children, within the heap a[0..size-1]. */
    private void siftDown(int[] a, int i, int size, String phase) {
        while (true) {
            int child = 2 * i + 1;
            if (child >= size) return;
            if (child + 1 < size) {
                frame(a, phase + ": which child is larger, " + a[child] + " or " + a[child + 1] + "?")
                        .range(0, size - 1).mark(Role.PIVOT, i).mark(Role.COMPARE, child, child + 1).save();
                if (less(a[child], a[child + 1])) child++;
            }
            frame(a, phase + ": compare parent " + a[i] + " with child " + a[child])
                    .range(0, size - 1).mark(Role.PIVOT, i).mark(Role.COMPARE, child).save();
            if (!less(a[i], a[child])) return;
            swap(a, i, child);
            frame(a, phase + ": swap parent and child").range(0, size - 1).mark(Role.SWAP, i, child).save();
            i = child;
        }
    }
}
