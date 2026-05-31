package org.example.backend;

import com.mongodb.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import org.example.backend.DTOs.AlphaVantage.AlphaVantageResponseDTO;
import org.example.backend.DTOs.TimeseriesDTO;
import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.DTOs.TwelveData.TwelveDataDTO;
import org.example.backend.Entities.Asset;
import org.example.backend.Entities.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Controller {
    private final IngestService ingestService;
    private final AnalyticsService analyticsService;

    @Autowired
    public Controller(IngestService ingestService, AnalyticsService analyticsService) {
        this.ingestService = ingestService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/assets")
    public ResponseEntity<List<Asset>> getAssets() {
        return ResponseEntity.ok(ingestService.getAllAssets());
    }

    // TODO implement fuzzy search
//    @GetMapping("/asset")
//    public ResponseEntity<Asset> getAssets(@RequestParam String assetSymbol) {
//        Optional<Asset> asset = ingestService.searchForAsset(assetSymbol);
//        return asset.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @GetMapping("/providers")
    public ResponseEntity<List<Provider>> getProviders() {
        return ResponseEntity.ok(ingestService.getAllProviders());
    }

    // TODO implement fuzzy search
//    @GetMapping("/provider")
//    public ResponseEntity<Provider> getProviders(@RequestParam String providerName) {
//        Optional<Provider> provider = ingestService.searchForProvider(providerName);
//        return provider.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//    }

    @GetMapping("/timeseries")
    public ResponseEntity<List<TimeseriesDTO>> getTimeseries(@RequestParam String assetId) {
        List<TimeseriesDTO> dto = ingestService.getTimeseries(assetId);
        if (dto == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(dto);
    }

    @PostMapping("/ingest/AlphaVantage")
    public ResponseEntity<AlphaVantageResponseDTO> ingestAlphaVantage(@RequestParam String assetName) {
        AlphaVantageResponseDTO dto = ingestService.ingestAlphaVantage(assetName);
        if (dto == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(dto);
    }

    @PostMapping("/ingest/TwelveData")
    public ResponseEntity<TwelveDataDTO> ingestTwelveData(@RequestParam String assetName) {
        TwelveDataDTO dto = ingestService.ingestTwelveData(assetName);
        if (dto == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(dto);
    }

    @GetMapping("/analyze")
    @Operation(summary = "Date variables are optional and should be of format YYYY-MM-DD")
    public ResponseEntity<TrendAnalysisDto> analyzeAsset(@RequestParam String assetId, @RequestParam @Nullable LocalDate startDate, @RequestParam @Nullable LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.analyzeAsset(assetId, startDate, endDate));
    }
}
