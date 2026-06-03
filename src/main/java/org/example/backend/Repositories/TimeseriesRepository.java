package org.example.backend.Repositories;

import org.example.backend.Entities.Timeseries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeseriesRepository extends MongoRepository<Timeseries, String> {

    List<Timeseries> findAllByAssetIdAndBusinessDateBetweenOrderByVersionDesc(String assetId, LocalDate start, LocalDate end);

    Optional<Timeseries> findByAssetIdAndSourceIdAndBusinessDateOrderByVersionDesc(String id, String id1, LocalDate date);

}
