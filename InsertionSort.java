public class InsertionSort extends SortingAlgorithm {

    public InsertionSort() {
        super("Insertion Sort", "O(n)", "O(n^2)", "O(n^2)", "O(1)");
    }

    @Override
    protected void execute(int[] a, int target) {
        for (int i = 1; i < a.length; i++) {
            int j = i;                               // move a[i] left until it is in place
            while (j > 0) {
                frame(a, "Compare " + a[j - 1] + " and " + a[j]).mark(Role.COMPARE, j - 1, j).save();
                if (less(a[j], a[j - 1])) {
                    swap(a, j, j - 1);
                    frame(a, "Move the element left").mark(Role.SWAP, j - 1, j).save();
                    j--;
                } else {
                    break;
                }
            }
        }
        finish(a);
    }
}
