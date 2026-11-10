import java.util.Arrays;
import java.util.Locale;

public class Benchmark {

    private static final int[] ARRAY_SIZES = {100, 1000, 10000};
    private static final int WARMUP_RUNS = 3;
    private static final int MEASUREMENT_RUNS = 10;
    
    private static final JsonExporter exporter = new JsonExporter();

    public static void main(String[] args) {
        Sorter<?>[] sorters = new Sorter<?>[]{
            new BubbleSortPassPerItem<>(),
            new BubbleSortUntilNoChange<>(),
            new BubbleSortWhileNeeded<>()
        };

        System.out.println("Warming up JVM JIT compiler...");
        warmup(sorters);

        DataProvider<?>[] dataProviders = {
            new DataProvider.IntegerProvider(),
            new DataProvider.StringProvider(),
            new DataProvider.ComplexEntityProvider()
        };

        for (int size : ARRAY_SIZES) {
            for (DataProvider<?> provider : dataProviders) {
                runBenchmarkSuite(provider, size, sorters);
            }
        }
        
        exporter.save("results.json");
    }

    private static <T extends Comparable<T>> void runBenchmarkSuite(DataProvider<T> provider, int size, Sorter<?>[] sorters) {
        System.out.println("\n--- Data Type: " + provider.getTypeName() + " (Size: " + size + ") ---");
        
        runBenchmark("Random (Unpredictable)", provider, provider.generateRandom(size), size, sorters);
        runBenchmark("Already Sorted (Best Case)", provider, provider.generateSorted(size), size, sorters);
        runBenchmark("Reverse Sorted (Worst Case)", provider, provider.generateReverse(size), size, sorters);
        runBenchmark("Almost Sorted (Few Out of Place)", provider, provider.generateAlmostSorted(size), size, sorters);
    }

    private static <T extends Comparable<T>> void runBenchmark(String inputType, DataProvider<T> provider, T[] originalData, int size, Sorter<?>[] sorters) {
        System.out.println("\n  Input Type: " + inputType);
        for (Sorter<?> rawSorter : sorters) {
            @SuppressWarnings("unchecked")
            Sorter<T> sorter = (Sorter<T>) rawSorter;
            long totalTimeNs = 0;

            for (int r = 0; r < MEASUREMENT_RUNS; r++) {
                T[] copy = Arrays.copyOf(originalData, originalData.length);

                long start = System.nanoTime();
                sorter.sort(copy);
                long end = System.nanoTime();

                totalTimeNs += (end - start);
            }

            double avgTimeMs = (totalTimeNs / (double) MEASUREMENT_RUNS) / 1_000_000.0;
            String algorithmName = sorter.getClass().getSimpleName();
            
            System.out.printf(Locale.US, "    %-30s : %10.2f ms%n", algorithmName, avgTimeMs);
            exporter.addEntry(size, provider.getTypeName(), inputType, algorithmName, avgTimeMs);
        }
    }

    private static void warmup(Sorter<?>[] sorters) {
        DataProvider.IntegerProvider intProvider = new DataProvider.IntegerProvider();
        Integer[] dummyData = intProvider.generateRandom(2000);
        for (int i = 0; i < WARMUP_RUNS; i++) {
            for (Sorter<?> rawSorter : sorters) {
                @SuppressWarnings("unchecked")
                Sorter<Integer> sorter = (Sorter<Integer>) rawSorter;
                Integer[] copy = Arrays.copyOf(dummyData, dummyData.length);
                sorter.sort(copy);
            }
        }
    }
}