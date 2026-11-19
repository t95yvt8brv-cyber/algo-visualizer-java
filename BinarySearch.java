public class BinarySearch extends SearchingAlgorithm {

    public BinarySearch() {
        super("Binary Search", "O(1)", "O(log n)", "O(log n)", "O(1)", true);   // needs a sorted array
    }

    @Override
    protected void execute(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            comparisons++;
            frame(a, "Middle a[" + mid + "] = " + a[mid] + ", target " + target)
                    .range(lo, hi).mark(Role.PIVOT, mid).save();
            if (a[mid] == target) {
                frame(a, "Found " + target + " at index " + mid).mark(Role.FOUND, mid).save();
                return;
            }
            if (a[mid] < target) lo = mid + 1;       // discard the left half
            else hi = mid - 1;                       // discard the right half
        }
        frame(a, target + " is not in the array").save();
    }
}
