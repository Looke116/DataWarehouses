package org.example.backend;

import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.Services.AnalyticsService;
import org.example.backend.Services.ChatService;
import org.example.backend.Services.CommonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommonService commonService;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private ChatService chatService;

    @Test
    void getAssets_ShouldReturnAssetMap() throws Exception {
        Map<String, String> mockAssets = new HashMap<>();
        mockAssets.put("123", "AAPL");
        Mockito.when(commonService.getAllAssets()).thenReturn(mockAssets);

        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['123']").value("AAPL"));
    }

    @Test
    void analyzeAsset_ShouldReturnTrendAnalysis() throws Exception {
        TrendAnalysisDto dto = new TrendAnalysisDto();
        dto.setAssetId("AAPL-ID");
        dto.setCurrentPrice(150.0);
        dto.setRiskClassification("LOW");

        Mockito.when(analyticsService.analyzeAsset(
                Mockito.eq("AAPL-ID"),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class))
        ).thenReturn(dto);

        mockMvc.perform(get("/analyze")
                        .param("assetId", "AAPL-ID")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetId").value("AAPL-ID"))
                .andExpect(jsonPath("$.currentPrice").value(150.0))
                .andExpect(jsonPath("$.riskClassification").value("LOW"));
    }

    @Test
    void askAssistant_ShouldReturnAIResponse() throws Exception {
        Mockito.when(chatService.chat("What is the trend for AAPL?"))
                .thenReturn("AAPL is currently bullish.");

        mockMvc.perform(post("/chat")
                        .param("message", "What is the trend for AAPL?"))
                .andExpect(status().isOk())
                .andExpect(content().string("AAPL is currently bullish."));
    }
}