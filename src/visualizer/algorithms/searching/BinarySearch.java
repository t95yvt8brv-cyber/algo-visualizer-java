package visualizer.algorithms.searching;

import visualizer.algorithms.SearchingAlgorithm;

/** Looks at the middle of the range and throws away the half that cannot hold the target. */
public class BinarySearch extends SearchingAlgorithm {

    public BinarySearch() {
        super("Binary Search", "O(1)", "O(log n)", "O(log n)", "O(1)", true);   // needs a sorted array
    }

    @Override
    protected void execute(int[] a, int target) {
        binarySearch(a, 0, a.length - 1, target);
    }
}
