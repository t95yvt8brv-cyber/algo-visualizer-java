public class QuickSort extends SortingAlgorithm {

    public QuickSort() {
        super("Quick Sort", "O(n log n)", "O(n log n)", "O(n^2)", "O(log n)");
    }

    @Override
    protected void execute(int[] a, int target) {
        sort(a, 0, a.length - 1);
        finish(a);
    }

    private void sort(int[] a, int lo, int hi) {
        if (lo > hi) return;
        if (lo == hi) {                              // a single element is already in place
            roles[lo] = Role.SORTED;
            return;
        }
        int pivot = a[hi];                           // Lomuto partition, last element is the pivot
        int i = lo;
        for (int j = lo; j < hi; j++) {
            frame(a, "Compare " + a[j] + " with pivot " + pivot)
                    .mark(Role.SWAP, i).mark(Role.PIVOT, hi).mark(Role.COMPARE, j).save();
            if (less(a[j], pivot)) {
                if (i != j) {
                    swap(a, i, j);
                    frame(a, "Swap").mark(Role.PIVOT, hi).mark(Role.SWAP, i, j).save();
                }
                i++;
            }
        }
        if (i != hi) swap(a, i, hi);
        roles[i] = Role.SORTED;                      // the pivot is now in its final place
        frame(a, "Pivot " + pivot + " is now in its final place").save();
        sort(a, lo, i - 1);
        sort(a, i + 1, hi);
    }
}
