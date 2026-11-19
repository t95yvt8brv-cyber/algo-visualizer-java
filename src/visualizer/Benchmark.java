package visualizer;

import java.util.Random;
import visualizer.algorithms.Algorithm;
import visualizer.algorithms.AlgorithmFactory;

/**
 * Counts comparisons for every sort without the GUI, on random and on reversed input.
 * Counting operations (not seconds) gives the same answer on every machine.
 * Run with:  java -cp out visualizer.Benchmark
 */
public class Benchmark {
    private static final int[] SIZES = {100, 200, 400, 800};

    public static void main(String[] args) {
        table("random data", n -> {
            Random random = new Random(n);           // same seed per size: every sort gets identical input
            int[] d = new int[n];
            for (int i = 0; i < n; i++) d[i] = random.nextInt(10 * n);
            return d;
        });
        table("reversed data", n -> {
            int[] d = new int[n];
            for (int i = 0; i < n; i++) d[i] = n - i;
            return d;
        });
        System.out.println("Doubling n roughly quadruples O(n^2) sorts but only a little more than doubles O(n log n) sorts.");
        System.out.println("Counting and radix sort make no comparisons at all. On reversed data the last-element pivot");
        System.out.println("makes Quick Sort O(n^2); median-of-3 and random pivots fix that.");
    }

    private static void table(String title, java.util.function.IntFunction<int[]> input) {
        System.out.printf("%n%-28s", "Comparisons, " + title);
        for (int n : SIZES) System.out.printf("%10s", "n=" + n);
        System.out.println();
        for (String name : AlgorithmFactory.names()) {
            if (AlgorithmFactory.create(name).needsTarget()) continue;     // sorts only
            System.out.printf("%-28s", name);
            for (int n : SIZES) {
                Algorithm algo = AlgorithmFactory.create(name).withoutRecording();
                algo.run(input.apply(n), 0);
                System.out.printf("%10d", algo.getComparisons());
            }
            System.out.println();
        }
    }
}
