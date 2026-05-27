package org.example.backend.Repositories;

import org.example.backend.Entities.Timeseries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface TimeseriesRepository extends MongoRepository<Timeseries, String> {

    boolean existsByAssetIdAndSourceIdAndBusinessDate(String assetId, String sourceId, LocalDate businessDate);

}
