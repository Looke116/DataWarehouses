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
    private String description;
    private Map<String, String> attributes;
    private LocalDate dateCreated;
    private boolean deleted;

    public Asset() {}

    public Asset(String symbol, String description, Map<String, String> attributes) {
        this.symbol = symbol;
        this.description = description;
        this.attributes = attributes;

        dateCreated = LocalDate.now();
        deleted = false;
    }
}
