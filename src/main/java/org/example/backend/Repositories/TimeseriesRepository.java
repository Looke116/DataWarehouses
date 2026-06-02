package org.example.backend.Repositories;

import org.example.backend.Entities.Timeseries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeseriesRepository extends MongoRepository<Timeseries, String> {

    boolean existsByAssetIdAndSourceIdAndBusinessDate(String assetId, String sourceId, LocalDate businessDate);

    List<Timeseries> findAllByAssetIdAndDeleted(String assetId, boolean deleted);

    List<Timeseries> findAllByAssetIdAndDeletedAndBusinessDateBetween(String assetId, boolean deleted, LocalDate start, LocalDate end);

    Optional<Timeseries> findByAssetIdAndSourceIdAndBusinessDateAndDeleted(String id, String id1, LocalDate date, boolean deleted);

    List<Timeseries> findByAssetIdAndDeletedOrderByBusinessDateAsc(String assetId, boolean deleted);

    List<Timeseries> findByAssetIdAndDeletedAndBusinessDateBetweenOrderByBusinessDateAsc(String assetId, boolean deleted, LocalDate start, LocalDate end);


}
