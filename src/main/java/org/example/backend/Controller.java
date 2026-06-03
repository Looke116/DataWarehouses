package org.example.backend;

import com.mongodb.lang.Nullable;
import io.swagger.v3.oas.annotations.Operation;
import org.example.backend.DTOs.TimeseriesDTO;
import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.Entities.Asset;
import org.example.backend.Entities.Provider;
import org.example.backend.Services.AnalyticsService;
import org.example.backend.Services.ChatService;
import org.example.backend.Services.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Controller {
    private final CommonService commonService;
    private final AnalyticsService analyticsService;
    private final ChatService chatService;

    @Autowired
    public Controller(CommonService commonService, AnalyticsService analyticsService, ChatService chatService) {
        this.commonService = commonService;
        this.analyticsService = analyticsService;
        this.chatService = chatService;
    }

    @GetMapping("/assets")
    public ResponseEntity<Map<String, String>> getAssets() {
        return ResponseEntity.ok(commonService.getAllAssets());
    }

    @GetMapping("/asset")
    public ResponseEntity<Asset> getAssets(@RequestParam String id) {
        Optional<Asset> asset = commonService.searchForAsset(id);
        return asset.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/providers")
    public ResponseEntity<Map<String, String>> getProviders() {
        return ResponseEntity.ok(commonService.getAllProviders());
    }

    @GetMapping("/provider")
    public ResponseEntity<Provider> getProviders(@RequestParam String id) {
        Optional<Provider> provider = commonService.searchForProvider(id);
        return provider.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/timeseries")
    @Operation(summary = "The date variables are optional and should be of format YYYY-MM-DD")
    public ResponseEntity<List<TimeseriesDTO>> getTimeseriesRange(@RequestParam String assetId, @RequestParam @Nullable LocalDate start, @RequestParam @Nullable LocalDate end) {
        List<TimeseriesDTO> dto = commonService.getTimeseries(assetId, start, end);
        if (dto == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(dto);
    }

    @PostMapping("/ingest")
    public ResponseEntity<Object> ingest(@RequestParam String assetName, @RequestParam Providers provider) {
        Object dto = null;
        if (provider == Providers.TwelveData)
            dto = commonService.ingestTwelveData(assetName);
        else if (provider == Providers.AlphaVantage)
            dto = commonService.ingestAlphaVantage(assetName);

        if (dto == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(dto);
    }

    @GetMapping("/analyze")
    @Operation(summary = "The date variables are optional and should be of format YYYY-MM-DD")
    public ResponseEntity<TrendAnalysisDto> analyzeAsset(@RequestParam String assetId, @RequestParam @Nullable LocalDate startDate, @RequestParam @Nullable LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.analyzeAsset(assetId, startDate, endDate));
    }

    @PostMapping("/chat")
    public ResponseEntity<String> askAssistant(@RequestParam String message) {
        return ResponseEntity.ok(chatService.chat(message));
    }
}
