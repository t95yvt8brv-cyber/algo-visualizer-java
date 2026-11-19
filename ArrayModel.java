import java.util.Random;

/** Owns the data being visualised, kept separate from the GUI (separation of concerns). */
public class ArrayModel {

    public enum Kind {
        RANDOM("Random"), NEARLY_SORTED("Nearly sorted"), REVERSED("Reversed"), FEW_UNIQUE("Few unique");

        private final String label;
        Kind(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private final Random random = new Random();
    private int[] data = new int[0];

    public void generate(Kind kind, int n) {
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

    private static void swap(int[] d, int i, int j) {
        int t = d[i];
        d[i] = d[j];
        d[j] = t;
    }

    public int[] copy() { return data.clone(); }
}
