package org.example.backend.Entities;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class Source {
    @Id
    public String id;
    public String name;
    public String description;
    public Date created;
    public List<String> attributes;

    public Source(String name, String description, Date created, List<String> attributes) {
        this.name = name;
        this.description = description;
        this.created = created;
        this.attributes = attributes;
    }
}
