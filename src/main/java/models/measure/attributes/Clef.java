package models.measure.attributes;

import lombok.Data;

@Data
public class Clef {
    public String sign;
    public int line;

    public Clef(String sign, int line){
        this.sign = sign;
        this.line = line;
    }
}
