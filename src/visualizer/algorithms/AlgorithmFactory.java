package visualizer.algorithms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import visualizer.algorithms.searching.BinarySearch;
import visualizer.algorithms.searching.ExponentialSearch;
import visualizer.algorithms.searching.InterpolationSearch;
import visualizer.algorithms.searching.JumpSearch;
import visualizer.algorithms.searching.LinearSearch;
import visualizer.algorithms.sorting.BubbleSort;
import visualizer.algorithms.sorting.CocktailShakerSort;
import visualizer.algorithms.sorting.CountingSort;
import visualizer.algorithms.sorting.HeapSort;
import visualizer.algorithms.sorting.InsertionSort;
import visualizer.algorithms.sorting.LastPivot;
import visualizer.algorithms.sorting.MedianOfThreePivot;
import visualizer.algorithms.sorting.MergeSort;
import visualizer.algorithms.sorting.QuickSort;
import visualizer.algorithms.sorting.RadixSort;
import visualizer.algorithms.sorting.RandomPivot;
import visualizer.algorithms.sorting.SelectionSort;
import visualizer.algorithms.sorting.ShellSort;

/** Factory + registry: add one line here and a new algorithm appears in every view. */
public final class AlgorithmFactory {
    private static final Map<String, Supplier<Algorithm>> REGISTRY = new LinkedHashMap<>();

    static {
        // comparison sorts, O(n^2)
        add(BubbleSort::new);
        add(CocktailShakerSort::new);
        add(SelectionSort::new);
        add(InsertionSort::new);
        // comparison sorts, faster
        add(ShellSort::new);
        add(MergeSort::new);
        add(HeapSort::new);
        add(() -> new QuickSort(new LastPivot()));             // same class, different strategy
        add(() -> new QuickSort(new MedianOfThreePivot()));
        add(() -> new QuickSort(new RandomPivot()));
        // non-comparison sorts
        add(CountingSort::new);
        add(RadixSort::new);
        // searches
        add(LinearSearch::new);
        add(BinarySearch::new);
        add(JumpSearch::new);
        add(InterpolationSearch::new);
        add(ExponentialSearch::new);
    }

    private AlgorithmFactory() { }

    private static void add(Supplier<Algorithm> maker) {
        REGISTRY.put(maker.get().getName(), maker);
    }

    public static String[] names() {
        return REGISTRY.keySet().toArray(new String[0]);
    }

    /** Creates a fresh algorithm object; throws IllegalArgumentException for an unknown name. */
    public static Algorithm create(String name) {
        Supplier<Algorithm> maker = REGISTRY.get(name);
        if (maker == null) throw new IllegalArgumentException("Unknown algorithm: " + name);
        return maker.get();
    }
}
