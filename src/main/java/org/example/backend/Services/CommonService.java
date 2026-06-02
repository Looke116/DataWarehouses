package org.example.backend.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.backend.DTOs.AlphaVantage.AlphaVantageResponseDTO;
import org.example.backend.DTOs.TimeseriesDTO;
import org.example.backend.DTOs.TwelveData.TwelveDataDTO;
import org.example.backend.DTOs.TwelveData.ValueDTO;
import org.example.backend.Entities.Asset;
import org.example.backend.Entities.Provider;
import org.example.backend.Entities.Timeseries;
import org.example.backend.Providers;
import org.example.backend.Repositories.AssetRepository;
import org.example.backend.Repositories.ProviderRepository;
import org.example.backend.Repositories.TimeseriesRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonService {
    private final ProviderRepository providerRepository;
    private final AssetRepository assetRepository;
    private final TimeseriesRepository timeseriesRepository;

    @Autowired
    public CommonService(ProviderRepository providerRepository,
                         AssetRepository assetRepository,
                         TimeseriesRepository timeseriesRepository) {
        this.providerRepository = providerRepository;
        this.assetRepository = assetRepository;
        this.timeseriesRepository = timeseriesRepository;
    }

    public Map<String, String> getAllAssets() {
        List<Asset> allAssets = assetRepository.findAll();

        Set<String> symbols = allAssets.stream().map(Asset::getSymbol).collect(Collectors.toSet());

        Map<String, String> assets = new HashMap<>();
        for (String symbol : symbols) {
            Asset asset =assetRepository.findFirstBySymbolOrderByVersionDesc(symbol).get();
            assets.put(asset.getId(), asset.getSymbol());
        }
        return assets;
    }

    public Optional<Asset> searchForAsset(String id) {
        return assetRepository.findFirstByIdOrderByVersionDesc(id);
    }

    public Map<String, String> getAllProviders() {
        Map<String, String> providers = new HashMap<>();
        List<Provider> allProviders = providerRepository.findAll();
        for (Provider provider : allProviders) {
            providers.put(provider.getId(), provider.getName());
        }
        return providers;
    }

    public Optional<Provider> searchForProvider(String name) {
        return providerRepository.getProviderById(name);
    }

    public @Nullable List<TimeseriesDTO> getTimeseries(String assetId, LocalDate start, LocalDate end) {
        if (start == null) start = LocalDate.now();
        if (end == null) end = LocalDate.EPOCH;

        if (start.isAfter(end)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        Optional<Asset> assetOptional = assetRepository.findById(assetId);
        if (assetOptional.isPresent()) {
            Asset asset = assetOptional.get();
            List<Timeseries> timeseries = timeseriesRepository.findAllByAssetIdAndDeletedAndBusinessDateBetween(asset.getId(), false, start, end);
            return timeseries.stream().map(x ->
                    new TimeseriesDTO(x.getBusinessDate(), x.getValuesInt(), x.getValuesDouble(), x.getValuesText())).toList();
        } else return null;
    }

    public AlphaVantageResponseDTO ingestAlphaVantage(@RequestParam String assetSymbol) {
        try (HttpClient client = HttpClient.newHttpClient(); Scanner scanner = new Scanner(new File("apikey"))) {
            String apikey = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts[0].equals(Providers.AlphaVantage.name())) {
                    apikey = parts[1];
                    break;
                }
            }

            if (apikey == null) return null;

            String url = "https://www.alphavantage.co/query" +
//                    "?function=" + "TIME_SERIES_DAILY" +
                    "?function=" + "TIME_SERIES_WEEKLY" +
                    "&symbol=" + assetSymbol +
                    "&apikey=" + apikey;

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).timeout(Duration.ofSeconds(10)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            AlphaVantageResponseDTO dto = mapper.readValue(response.body(), AlphaVantageResponseDTO.class);

            Provider provider = providerRepository.getProviderByName(Providers.AlphaVantage.name());
            Asset asset = createOrUpdateAsset(assetSymbol, provider, null, dto.getMetaData().getTimeZone());

            for (String dateString : dto.getTimeSeries().keySet()) {
                LocalDate date = LocalDate.parse(dateString);

                Map<String, Integer> mapInteger = new HashMap<>();
                mapInteger.put("Volume", Integer.valueOf(dto.getTimeSeries().get(dateString).getVolume()));

                Map<String, Double> mapDouble = new HashMap<>();
                mapDouble.put("Open", Double.valueOf(dto.getTimeSeries().get(dateString).getOpen()));
                mapDouble.put("High", Double.valueOf(dto.getTimeSeries().get(dateString).getHigh()));
                mapDouble.put("Low", Double.valueOf(dto.getTimeSeries().get(dateString).getLow()));
                mapDouble.put("Close", Double.valueOf(dto.getTimeSeries().get(dateString).getClose()));

                createOrUpdateTimeseries(provider, asset, date, mapInteger, mapDouble);
            }

            return dto;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public TwelveDataDTO ingestTwelveData(@RequestParam String assetSymbol) {
        try (HttpClient client = HttpClient.newHttpClient(); Scanner scanner = new Scanner(new File("apikey"))) {
            String apikey = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts[0].equals(Providers.TwelveData.name())) {
                    apikey = parts[1];
                    break;
                }
            }

            if (apikey == null) return null;

            String url = "https://api.twelvedata.com/time_series" +
                    "?symbol=" + assetSymbol +
                    "&interval=" + "1week" +
                    "&outputsize=" + "1000" +
                    "&apikey=" + apikey;

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).timeout(Duration.ofSeconds(10)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body().substring(0, 1000));
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            TwelveDataDTO dto = mapper.readValue(response.body(), TwelveDataDTO.class);

            Provider provider = providerRepository.getProviderByName(Providers.TwelveData.name());
            Asset asset = createOrUpdateAsset(assetSymbol, provider, dto.getMeta().getType(), dto.getMeta().getExchangeTimezone());

            for (ValueDTO value : dto.getValues()) {
                LocalDate date = value.getDatetime();

                Map<String, Integer> mapInteger = new HashMap<>();
                mapInteger.put("Volume", value.getVolume());

                Map<String, Double> mapDouble = new HashMap<>();
                mapDouble.put("Open", value.getOpen());
                mapDouble.put("High", value.getHigh());
                mapDouble.put("Low", value.getLow());
                mapDouble.put("Close", value.getClose());

                createOrUpdateTimeseries(provider, asset, date, mapInteger, mapDouble);
            }

            return dto;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Asset createOrUpdateAsset(String assetSymbol, Provider provider, String type, String timezone) {
        if (!assetRepository.existsBySymbol(assetSymbol)) {
            Map<String, String> map = new HashMap<>();
            map.put(provider.getName(), String.valueOf(provider.getAttributes()));

            return assetRepository.save(new Asset(assetSymbol, type, timezone, map));
        } else {
            Asset asset = assetRepository.findFirstBySymbolOrderByVersionDesc(assetSymbol).get();

            if (asset.getAttributes().get(provider.getName()) == null) {
                Map<String, String> map = asset.getAttributes();
                map.put(provider.getName(), String.valueOf(provider.getAttributes()));
                asset = new Asset(asset.getSymbol(), asset.getType(), asset.getTimezone(), map, asset.getVersion() + 1);
                assetRepository.save(asset);
            }
            return asset;
        }

    }

    private void createOrUpdateTimeseries(Provider provider, Asset asset, LocalDate date, Map<String, Integer> mapInteger, Map<String, Double> mapDouble) {
        Optional<Timeseries> timeseriesOptional = timeseriesRepository.findByAssetIdAndSourceIdAndBusinessDateAndDeleted(asset.getId(), provider.getId(), date, false);
        if (timeseriesOptional.isEmpty()) {
            timeseriesRepository.save(new Timeseries(asset.getId(), provider.getId(), date, mapInteger, mapDouble));
        } else {
            Timeseries old = timeseriesOptional.get();
            boolean update = false;
            if (!old.getValuesInt().equals(mapInteger)) update = true;
            if (!old.getValuesDouble().equals(mapDouble)) update = true;

            if (update) {
                old.setDeleted(true);
                timeseriesRepository.save(old);
                timeseriesRepository.save(new Timeseries(asset.getId(), provider.getId(), date, mapInteger, mapDouble));
            }
        }
    }
}
