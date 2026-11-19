package visualizer.model;

import java.util.Arrays;
import java.util.Random;

/** Owns the data being visualised, kept separate from the GUI (separation of concerns). */
public class ArrayModel {

    public enum Kind {
        RANDOM("Random"), NEARLY_SORTED("Nearly sorted"), REVERSED("Reversed"), FEW_UNIQUE("Few unique");

        private final String label;
        Kind(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public static final int MIN_SIZE = 2, MAX_SIZE = 100, MAX_VALUE = 999;

    private final Random random = new Random();
    private int[] data = new int[0];

    /** Fills the model with n values between 1 and n arranged as the given kind. */
    public void generate(Kind kind, int n) {
        if (n < 1) throw new IllegalArgumentException("size must be at least 1");
        int[] d = new int[n];
        switch (kind) {
            case REVERSED:
                for (int i = 0; i < n; i++) d[i] = n - i;
                break;
            case NEARLY_SORTED:
                for (int i = 0; i < n; i++) d[i] = i + 1;
                for (int k = 0; k < Math.max(1, n / 10); k++) swap(d, random.nextInt(n), random.nextInt(n));
                break;
            case FEW_UNIQUE:
                for (int i = 0; i < n; i++) d[i] = Math.max(1, (1 + random.nextInt(4)) * n / 4);
                break;
            default:                                  // RANDOM: shuffle 1..n (Fisher-Yates)
                for (int i = 0; i < n; i++) d[i] = i + 1;
                for (int i = n - 1; i > 0; i--) swap(d, i, random.nextInt(i + 1));
        }
        data = d;
    }

    /** Uses values typed by the user instead of generated ones. */
    public void setValues(int[] values) {
        check(values);
        data = values.clone();
    }

    /**
     * Reads numbers typed by the user, e.g. "5, 3 8,1". Commas and spaces both separate values.
     * Throws IllegalArgumentException with a message that can be shown to the user as it is.
     */
    public static int[] parse(String text) {
        String[] parts = Arrays.stream(text.trim().split("[,;\\s]+"))
                .filter(part -> !part.isEmpty())
                .toArray(String[]::new);
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                values[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("\"" + parts[i] + "\" is not a whole number");
            }
        }
        check(values);
        return values;
    }

    private static void check(int[] values) {
        if (values.length < MIN_SIZE) {
            throw new IllegalArgumentException("Type at least " + MIN_SIZE + " numbers, e.g. 5, 3, 8, 1");
        }
        if (values.length > MAX_SIZE) {
            throw new IllegalArgumentException("At most " + MAX_SIZE + " numbers (you typed " + values.length + ")");
        }
        for (int v : values) {
            if (v < 0 || v > MAX_VALUE) {
                throw new IllegalArgumentException("Numbers must be between 0 and " + MAX_VALUE + " (got " + v + ")");
            }
        }
    }

    private static void swap(int[] d, int i, int j) {
        int t = d[i];
        d[i] = d[j];
        d[j] = t;
    }

    public int size()   { return data.length; }
    public int[] copy() { return data.clone(); }
}
