package org.example.backend.Services;

import org.example.backend.DTOs.TimeseriesDTO;
import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.Entities.Asset;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final CommonService commonService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ChatService(ChatClient.Builder chatClientBuilder, CommonService commonService, AnalyticsService analyticsService) {
        this.commonService = commonService;
        this.analyticsService = analyticsService;

        String systemText = """
                You are a financial data platform AI assistant.
                Your job is to help users explore and understand the data warehouse using natural language.
                You MUST ground your answers strictly in the platform's data provided via your available tools.
                Do not make up generic financial text or historical prices. If you do not have the data, ask the user to ingest it or tell them it is missing.
                """;

        this.chatClient = chatClientBuilder
                .defaultSystem(systemText)
                .build();
    }

    public String chat(String userMessage) {
        return this.chatClient.prompt()
                .user(userMessage)
                .tools(this)
                .call()
                .content();
    }

    @Tool(description = "List all available assets matching their database IDs and ticker symbols.")
    public Map<String, String> listAllAssets() {
        return commonService.getAllAssets();
    }

    @Tool(description = "Fetch specific structural metadata details for a given asset ID.")
    public Asset getAssetDetails(String id) {
        Optional<Asset> asset = commonService.searchForAsset(id);
        return asset.orElse(null);
    }

    @Tool(description = "Fetch time-series pricing data points (Open, Close, High, Low, Volume) for a specified asset ID and date range (YYYY-MM-DD). Date filters are optional.")
    public List<TimeseriesDTO> fetchTimeseries(String assetId, String startDateStr, String endDateStr) {
        LocalDate start = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate end = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;
        return commonService.getTimeseries(assetId, start, end);
    }

    @Tool(description = "Compute and summarize price trends, statistical calculations, volatility indexes, risk classification, and next-day price forecasting for a specified asset ID.")
    public TrendAnalysisDto analyzeAssetPerformance(String assetId, String startDateStr, String endDateStr) {
        LocalDate start = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate end = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;
        return analyticsService.analyzeAsset(assetId, start, end);
    }
}