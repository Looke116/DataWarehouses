package org.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.DTOs.AlphaVantage.AlphaVantageResponseDTO;
import org.example.backend.DTOs.AlphaVantage.DailyPrice;
import org.example.backend.Entities.Asset;
import org.example.backend.Entities.Source;
import org.example.backend.Entities.Timeseries;
import org.example.backend.Repositories.AssetRepository;
import org.example.backend.Repositories.SourceRepository;
import org.example.backend.Repositories.TimeseriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
public class Controller {
    SourceRepository sourceRepository;
    AssetRepository assetRepository;
    TimeseriesRepository timeseriesRepository;


    public Controller(
            @Autowired AssetRepository assetRepository,
            @Autowired SourceRepository srcRepository,
            @Autowired TimeseriesRepository timeseriesRepository, SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
        this.assetRepository = assetRepository;
        this.timeseriesRepository = timeseriesRepository;
    }

//    @GetMapping("/assets")
//    public ResponseEntity<> getAssets() {
//    }
//
//    @GetMapping("/assets/{id}")
//    public ResponseEntity<Object> getAsset(@PathVariable("id") int id) {
//    }
//
//    @GetMapping("/providers")
//    public ResponseEntity<> getProviders() {
//    }
//
//    @GetMapping("/timeseries?asset=BTC")
//    public ResponseEntity<> getTimeseries(@RequestParam String asset) {
//    }

    @GetMapping("/ingest")
    public ResponseEntity<AlphaVantageResponseDTO> ingest(@RequestParam String assetName, @RequestParam Sources provider) {

        assetName = "IBM";
        provider = Sources.AlphaVantage;

        try (HttpClient client = HttpClient.newHttpClient()) {
            String url = "https://www.alphavantage.co/query" +
                    "?function=" + "TIME_SERIES_DAILY" +
                    "&symbol=" + assetName +
//                    "&apikey=" + "demo";
                    "&apikey=" + new Scanner(new File("apikey")).nextLine();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            AlphaVantageResponseDTO dto = mapper.readValue(response.body(), AlphaVantageResponseDTO.class);

            Source source = sourceRepository.getSourceByName(provider.name());
            Map<String, String> map = new HashMap<>();
            map.put(source.name, String.valueOf(source.attributes));

            Asset asset;
            if (!assetRepository.existsByName(assetName)) {
                asset = assetRepository.save(new Asset(assetName, "", Date.from(Instant.now()), map));
            }else {
                asset = assetRepository.findByName(assetName);
            }

            for (String dateString : dto.getTimeSeries().keySet()) {
                LocalDate date = LocalDate.parse(dateString);

                if (!timeseriesRepository.existsByAssetIdAndSourceIdAndBusinessDate(asset.id, source.id, date)) {
                    Map<String, Integer> mapInteger = new HashMap<>();
                    mapInteger.put("Volume", Integer.valueOf(dto.getTimeSeries().get(dateString).getVolume()));

                    Map<String, Double> mapDouble = new HashMap<>();
                    mapDouble.put("Open", Double.valueOf(dto.getTimeSeries().get(dateString).getOpen()));
                    mapDouble.put("High", Double.valueOf(dto.getTimeSeries().get(dateString).getHigh()));
                    mapDouble.put("Low", Double.valueOf(dto.getTimeSeries().get(dateString).getLow()));
                    mapDouble.put("Close", Double.valueOf(dto.getTimeSeries().get(dateString).getClose()));

                    timeseriesRepository.save(new Timeseries(asset.id, source.id, date, mapInteger, mapDouble));
                }
            }

            return ResponseEntity.ok(dto);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}