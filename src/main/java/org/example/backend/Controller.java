package org.example.backend;

import org.example.backend.DTOs.AlphaVantage.AlphaVantageResponseDTO;
import org.example.backend.DTOs.TimeseriesDTO;
import org.example.backend.DTOs.TwelveData.TwelveDataDTO;
import org.example.backend.Entities.Asset;
import org.example.backend.Entities.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Controller {
    private final IngestService ingestService;

    @Autowired
    public Controller(IngestService ingestService) {
        this.ingestService = ingestService;
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
}
