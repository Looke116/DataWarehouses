package org.example.backend.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

@Data
public class Asset {
    @Id
    public String id;
    public String name;
    public String description;
    public Date dateCreated;
    public Map<String, String> attributes;

    public Asset() {}

    public Asset(String name, String description, Date dateCreated, Map<String, String> attributes) {
        this.name = name;
        this.description = description;
        this.dateCreated = dateCreated;
        this.attributes = attributes;
    }
}
