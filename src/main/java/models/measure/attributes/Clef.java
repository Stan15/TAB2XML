package models.measure.attributes;

public class Clef {
    public String sign;
    public int line;

    public Clef(String sign, int line){
        this.sign = sign;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public String getSign() {
        return sign;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
