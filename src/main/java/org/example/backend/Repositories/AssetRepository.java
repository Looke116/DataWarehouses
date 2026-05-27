package org.example.backend.Repositories;

import org.example.backend.Entities.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssetRepository extends MongoRepository<Asset, String> {

    boolean existsByName(String name);

    Asset findByName(String name);
}
