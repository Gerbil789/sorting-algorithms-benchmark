import java.util.Random;

public abstract class DataProvider<T extends Comparable<T>> {
    public abstract String getTypeName();
    protected abstract T[] createArray(int size);
    protected abstract T generateRandomItem(Random rand, int size);
    protected abstract T generateSortedItem(int index);
    protected abstract T generateReverseItem(int index, int size);

    public T[] generateRandom(int size) {
        Random rand = new Random(42);
        T[] arr = createArray(size);
        for (int i = 0; i < size; i++) arr[i] = generateRandomItem(rand, size);
        return arr;
    }

    public T[] generateSorted(int size) {
        T[] arr = createArray(size);
        for (int i = 0; i < size; i++) arr[i] = generateSortedItem(i);
        return arr;
    }

    public T[] generateReverse(int size) {
        T[] arr = createArray(size);
        for (int i = 0; i < size; i++) arr[i] = generateReverseItem(i, size);
        return arr;
    }

    public T[] generateAlmostSorted(int size) {
        T[] arr = generateSorted(size);
        Random rand = new Random(42);
        int swaps = Math.max(1, size / 20); 
        for (int i = 0; i < swaps; i++) {
            int idx1 = rand.nextInt(size);
            int idx2 = rand.nextInt(size);
            T temp = arr[idx1];
            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }
        return arr;
    }

    // --- Concrete Providers ---

    public static class IntegerProvider extends DataProvider<Integer> {
        @Override public String getTypeName() { return "Integer"; }
        @Override protected Integer[] createArray(int size) { return new Integer[size]; }
        @Override protected Integer generateRandomItem(Random rand, int size) { return rand.nextInt(size * 10); }
        @Override protected Integer generateSortedItem(int index) { return index; }
        @Override protected Integer generateReverseItem(int index, int size) { return size - index; }
    }

    public static class StringProvider extends DataProvider<String> {
        @Override public String getTypeName() { return "String"; }
        @Override protected String[] createArray(int size) { return new String[size]; }
        @Override protected String generateRandomItem(Random rand, int size) {
            int len = 5 + rand.nextInt(6); 
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                sb.append((char) ('a' + rand.nextInt(26)));
            }
            return sb.toString();
        }
        @Override protected String generateSortedItem(int index) { return String.format("str_%08d", index); }
        @Override protected String generateReverseItem(int index, int size) { return String.format("str_%08d", size - index); }
    }

    public static class ComplexEntityProvider extends DataProvider<ComplexEntity> {
        @Override public String getTypeName() { return "Complex Entity"; }
        @Override protected ComplexEntity[] createArray(int size) { return new ComplexEntity[size]; }
        @Override protected ComplexEntity generateRandomItem(Random rand, int size) { 
            return new ComplexEntity(rand.nextInt(size * 10), "Mesh_" + rand.nextInt(100), "Layer_" + rand.nextInt(5), rand.nextDouble() * 100.0); 
        }
        @Override protected ComplexEntity generateSortedItem(int index) { 
            return new ComplexEntity(index, "Mesh_A", "Layer_0", (double) index); 
        }
        @Override protected ComplexEntity generateReverseItem(int index, int size) { 
            return new ComplexEntity(size - index, "Mesh_A", "Layer_0", (double) (size - index)); 
        }
    }
}