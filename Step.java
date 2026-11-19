/** One recorded animation frame: a snapshot of the array, a role per element, and a message. */
public class Step {
    private final int[] data;
    private final Role[] roles;
    private final String note;
    private final int comparisons;
    private final int writes;

    public Step(int[] data, Role[] roles, String note, int comparisons, int writes) {
        this.data = data;
        this.roles = roles;
        this.note = note;
        this.comparisons = comparisons;
        this.writes = writes;
    }

    public int[] getData()      { return data; }
    public Role[] getRoles()    { return roles; }
    public String getNote()     { return note; }
    public int getComparisons() { return comparisons; }
    public int getWrites()      { return writes; }
}
