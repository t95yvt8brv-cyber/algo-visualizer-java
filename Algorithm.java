import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract parent of every algorithm (abstraction).
 * Template Method pattern: run() does the set-up and returns the recorded steps,
 * subclasses only implement execute().
 */
public abstract class Algorithm {
    private final String name, best, average, worst, space;
    private final boolean needsSorted;

    protected int comparisons;      // statistics are encapsulated inside the object
    protected int writes;
    protected Role[] roles;         // roles that persist between frames (e.g. SORTED)
    private boolean recording = true;
    private List<Step> steps;

    protected Algorithm(String name, String best, String average, String worst,
                        String space, boolean needsSorted) {
        this.name = name;
        this.best = best;
        this.average = average;
        this.worst = worst;
        this.space = space;
        this.needsSorted = needsSorted;
    }

    /** Runs the algorithm on a copy of the input and returns every recorded frame. */
    public final List<Step> run(int[] input, int target) {
        int[] a = input.clone();
        comparisons = 0;
        writes = 0;
        roles = new Role[a.length];
        Arrays.fill(roles, Role.NORMAL);
        steps = new ArrayList<Step>();
        execute(a, target);
        return steps;
    }

    /** The algorithm itself. Change `a` in place and call frame(...).save() after each action. */
    protected abstract void execute(int[] a, int target);

    public String getName()      { return name; }
    public int getComparisons()  { return comparisons; }
    public int getWrites()       { return writes; }
    public boolean needsSorted() { return needsSorted; }
    public boolean needsTarget() { return false; }          // SearchingAlgorithm overrides this

    /** Turn recording off to count operations quickly (used by Benchmark). */
    public Algorithm withoutRecording() { recording = false; return this; }

    public String complexity() {
        return "Best " + best + " | Average " + average + " | Worst " + worst + " | Space " + space;
    }

    @Override
    public String toString() { return name; }

    // ---------- frame builder ----------
    protected Frame frame(int[] a, String note) { return new Frame(a, note); }

    /** Builds one Step: copy of the array + persistent roles + extra marks for this moment. */
    protected class Frame {
        private final int[] data;
        private final Role[] marks;
        private final String note;

        Frame(int[] a, String note) {
            this.data = a.clone();
            this.marks = roles.clone();
            this.note = note;
        }

        public Frame mark(Role role, int... indices) {
            for (int i : indices) marks[i] = role;
            return this;
        }

        /** Tint lo..hi (inclusive) as RANGE unless an element already has a special role. */
        public Frame range(int lo, int hi) {
            for (int i = lo; i <= hi; i++) if (marks[i] == Role.NORMAL) marks[i] = Role.RANGE;
            return this;
        }

        public void save() {
            if (recording) steps.add(new Step(data, marks, note, comparisons, writes));
        }
    }
}
