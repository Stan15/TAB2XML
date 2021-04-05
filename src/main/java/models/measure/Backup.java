package models.measure;

import lombok.Data;

@Data
public class Backup {
    int duration;
    public Backup(int duration) {
        this.duration = duration;
    }
}
