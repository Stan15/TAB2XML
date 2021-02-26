package utility;

public class Range {
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
}

