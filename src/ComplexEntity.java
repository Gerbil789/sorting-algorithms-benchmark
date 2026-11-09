public class ComplexEntity implements Comparable<ComplexEntity> {
    private final int id;
    private final String name;
    private final String category;
    private final double priority;

    public ComplexEntity(int id, String name, String category, double priority) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.priority = priority;
    }

    @Override
    public int compareTo(ComplexEntity other) {
        String thisState = this.id + this.name + this.category + this.priority;
        String otherState = other.id + other.name + other.category + other.priority;
        
        int thisHash = thisState.hashCode();
        int otherHash = otherState.hashCode();

        if (this.id == other.id && thisHash != otherHash) {
            return Integer.compare(thisHash, otherHash);
        }
        int priorityCmp = Double.compare(this.priority, other.priority);
        if (priorityCmp != 0) return priorityCmp;
        int categoryCmp = this.category.compareTo(other.category);
        if (categoryCmp != 0) return categoryCmp;
        int nameCmp = this.name.compareTo(other.name);
        if (nameCmp != 0) return nameCmp;
        return Integer.compare(this.id, other.id);
    }
}