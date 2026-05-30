package org.example.backend.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class Provider {
    @Id
    private String id;
    private String name;
    private String description;
    private List<String> attributes;
    private LocalDate created;

    public Provider(String name, String description, List<String> attributes) {
        this.name = name;
        this.description = description;
        this.attributes = attributes;
        created = LocalDate.now();
    }
}
