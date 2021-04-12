package utility;

public class Range implements Comparable<Range> {
    private int start;
    private int end;
    private int size;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
        this.size = this.end - this.start;
    }

    public int getStart() {return start;}
    public int getEnd() {return end;}
    public int getSize() {return size;}

    public boolean contains(int number) {
        return number >= start && number <= end;
    }
    public boolean contains(Range other) {
        return this.contains(other.start) && this.contains(other.end);
    }

    public boolean overlaps(Range other) {
        return (this.end<=other.end && this.end>other.start) || (this.start>=other.start && this.start<other.end) || this.contains(other) || other.contains(this);
    }

    @Override
    public int compareTo(Range other) {
        int val = this.start-other.start;
        if (val == 0) return this.size-other.size;
        return val;
    }
    @Override
    public boolean equals(Object o) {
        Range other = (Range) o;
        return (this.start == other.start) && (this.end == other.end);
    }
}

