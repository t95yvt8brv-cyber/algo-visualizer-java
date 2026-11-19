package visualizer.model;

/**
 * One recorded animation frame: a snapshot of the array, a role per element, a message and the
 * running counts. Immutable: the arrays are copied on the way in and on the way out, so nobody
 * can change a frame after it has been recorded (encapsulation).
 */
public final class Step {
    private final int[] data;
    private final Role[] roles;
    private final String note;
    private final int comparisons;
    private final int writes;

    public Step(int[] data, Role[] roles, String note, int comparisons, int writes) {
        if (data.length != roles.length) {
            throw new IllegalArgumentException("data and roles must have the same length");
        }
        this.data = data.clone();
        this.roles = roles.clone();
        this.note = note;
        this.comparisons = comparisons;
        this.writes = writes;
    }

    public int size()            { return data.length; }
    public int valueAt(int i)    { return data[i]; }
    public Role roleAt(int i)    { return roles[i]; }
    public int[] getData()       { return data.clone(); }
    public Role[] getRoles()     { return roles.clone(); }
    public String getNote()      { return note; }
    public int getComparisons()  { return comparisons; }
    public int getWrites()       { return writes; }
}
