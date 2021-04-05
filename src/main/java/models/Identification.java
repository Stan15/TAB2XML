package models;

import lombok.Data;

@Data
public class Identification {
    Creator creator;

    public Identification(Creator creator) {
        this.creator = creator;
    }
}
