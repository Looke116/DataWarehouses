package org.example.backend;

import org.example.backend.DTOs.TrendAnalysisDto;
import org.example.backend.Services.AnalyticsService;
import org.example.backend.Services.ChatService;
import org.example.backend.Services.CommonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private CommonService commonService;
    @Mock
    private AnalyticsService analyticsService;
    @Mock
    private ChatClient.Builder chatClientBuilder;

    @InjectMocks
    private ChatService chatService;

    @Test
    void analyzeAssetPerformance_Tool_ShouldParseStringsCorrectly() {
        TrendAnalysisDto mockTrend = new TrendAnalysisDto();
        mockTrend.setAssetId("TSLA");

        Mockito.when(analyticsService.analyzeAsset("TSLA", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1)))
                .thenReturn(mockTrend);

        TrendAnalysisDto result = chatService.analyzeAssetPerformance("TSLA", "2026-01-01", "2026-06-01");

        assertNotNull(result);
        assertEquals("TSLA", result.getAssetId());
    }
}