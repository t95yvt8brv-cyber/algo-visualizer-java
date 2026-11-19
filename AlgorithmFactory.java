import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Factory + registry: add one line here and a new algorithm appears in the GUI. */
public final class AlgorithmFactory {
    private static final Map<String, Supplier<Algorithm>> REGISTRY = new LinkedHashMap<String, Supplier<Algorithm>>();

    static {
        add(BubbleSort::new);
        add(SelectionSort::new);
        add(InsertionSort::new);
        add(MergeSort::new);
        add(QuickSort::new);
        add(LinearSearch::new);
        add(BinarySearch::new);
    }

    private AlgorithmFactory() { }

    private static void add(Supplier<Algorithm> maker) {
        REGISTRY.put(maker.get().getName(), maker);
    }

    public static String[] names() {
        return REGISTRY.keySet().toArray(new String[0]);
    }

    public static Algorithm create(String name) {
        return REGISTRY.get(name).get();
    }
}
