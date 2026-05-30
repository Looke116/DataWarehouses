package org.example.backend.Repositories;

import org.example.backend.Entities.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AssetRepository extends MongoRepository<Asset, String> {

    boolean existsBySymbol(String symbol);

    Optional<Asset> findBySymbolAndDeleted(String symbol, boolean deleted);

}
