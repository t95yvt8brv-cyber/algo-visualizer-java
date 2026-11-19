/** Parent of all searches. Overriding needsTarget() lets the GUI enable the target box (polymorphism). */
public abstract class SearchingAlgorithm extends Algorithm {

    protected SearchingAlgorithm(String name, String best, String average, String worst,
                                 String space, boolean needsSorted) {
        super(name, best, average, worst, space, needsSorted);
    }

    @Override
    public boolean needsTarget() { return true; }
}
