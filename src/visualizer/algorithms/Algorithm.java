package visualizer.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import visualizer.model.Role;
import visualizer.model.Step;

/**
 * Abstract parent of every algorithm (abstraction).
 * Template Method pattern: run() does the set-up and returns the recorded steps,
 * subclasses only implement execute().
 * All state is private; subclasses change it only through the protected helpers (encapsulation).
 */
public abstract class Algorithm {
    private final String name, best, average, worst, space;
    private final boolean needsSorted;

    private int comparisons;
    private int writes;
    private Role[] roles;           // roles that persist between frames (e.g. SORTED)
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

    /** Runs the algorithm on a copy of the input and returns every recorded frame (read-only). */
    public final List<Step> run(int[] input, int target) {
        if (needsSorted && !isSorted(input)) {
            throw new IllegalArgumentException(name + " needs a sorted array");
        }
        int[] a = input.clone();
        comparisons = 0;
        writes = 0;
        roles = new Role[a.length];
        Arrays.fill(roles, Role.NORMAL);
        steps = new ArrayList<>();
        execute(a, target);
        return Collections.unmodifiableList(steps);
    }

    /** The algorithm itself. Change `a` in place and call frame(...).at(line).save() after each action. */
    protected abstract void execute(int[] a, int target);

    /** The pseudocode shown next to the bars. Frame.at(n) highlights line n (1-based). */
    public abstract List<String> pseudocode();

    // ---------- public information ----------
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

    // ---------- helpers for subclasses ----------
    protected final void countComparison()        { comparisons++; }
    protected final void countWrites(int count)   { writes += count; }
    protected final void setRole(int i, Role role) { roles[i] = role; }
    protected final void setAllRoles(Role role)   { Arrays.fill(roles, role); }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    // ---------- frame builder ----------
    protected final Frame frame(int[] a, String note) { return new Frame(a, note); }

    /** Builds one Step: copy of the array + persistent roles + extra marks + pseudocode line. */
    protected final class Frame {
        private final int[] data;
        private final Role[] marks;
        private final String note;
        private int line;

        private Frame(int[] a, String note) {
            this.data = a.clone();
            this.marks = roles.clone();
            this.note = note;
        }

        /** The pseudocode line (1-based) this frame belongs to. */
        public Frame at(int line) {
            this.line = line;
            return this;
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
            if (recording) steps.add(new Step(data, marks, note, comparisons, writes, line));
        }
    }
}
