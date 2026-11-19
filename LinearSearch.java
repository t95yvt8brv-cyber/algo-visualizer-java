public class LinearSearch extends SearchingAlgorithm {

    public LinearSearch() {
        super("Linear Search", "O(1)", "O(n)", "O(n)", "O(1)", false);
    }

    @Override
    protected void execute(int[] a, int target) {
        for (int i = 0; i < a.length; i++) {
            comparisons++;
            frame(a, "Is a[" + i + "] = " + a[i] + " equal to " + target + "?").mark(Role.COMPARE, i).save();
            if (a[i] == target) {
                frame(a, "Found " + target + " at index " + i).mark(Role.FOUND, i).save();
                return;
            }
        }
        frame(a, target + " is not in the array").save();
    }
}
