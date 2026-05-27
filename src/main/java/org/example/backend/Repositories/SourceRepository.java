package org.example.backend.Repositories;

import org.example.backend.Entities.Source;
import org.example.backend.Sources;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SourceRepository extends MongoRepository<Source, String> {
    boolean existsByName(String provider);

    Source getSourceByName(String provider);

    Source findByName(String name);
}
