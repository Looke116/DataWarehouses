package org.example.backend.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Map;

@Data
public class Asset {
    @Id
    private String id;
    private String symbol;
    private String type;
    private String timezone;
    private Map<String, String> attributes;
    private LocalDate dateCreated;
    private int version;

    public Asset() {}

    public Asset(String symbol, String type, String timezone, Map<String, String> attributes) {
        this.symbol = symbol;
        this.attributes = attributes;
        this.type = type;
        this.timezone = timezone;

        dateCreated = LocalDate.now();
        version = 0;
    }
    public Asset(String symbol, String type, String timezone, Map<String, String> attributes, int version) {
        this.symbol = symbol;
        this.attributes = attributes;
        this.type = type;
        this.timezone = timezone;
        this.version = version;

        dateCreated = LocalDate.now();
    }
}
