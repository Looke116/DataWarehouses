package org.example.backend;

import org.example.backend.Entities.Asset;
import org.example.backend.Repositories.AssetRepository;
import org.example.backend.Repositories.ProviderRepository;
import org.example.backend.Repositories.TimeseriesRepository;
import org.example.backend.Services.CommonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    @Mock
    private AssetRepository assetRepository;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private TimeseriesRepository timeseriesRepository;

    @InjectMocks
    private CommonService commonService;

    private Asset mockAsset;

    @BeforeEach
    void setUp() {
        mockAsset = new Asset("AAPL", "Stock", "EST", Collections.emptyMap());
        mockAsset.setId("asset-123");
    }

    @Test
    void getTimeseries_ShouldSwapDates_WhenStartIsAfterEnd() {
        LocalDate start = LocalDate.of(2026, 06, 01);
        LocalDate end = LocalDate.of(2026, 01, 01);

        Mockito.when(assetRepository.findById("asset-123")).thenReturn(Optional.of(mockAsset));

        Mockito.when(timeseriesRepository.findByAssetIdAndBusinessDateBetweenOrderByVersionDesc(
                Mockito.eq("asset-123"),
                Mockito.eq(end),
                Mockito.eq(start)
        )).thenReturn(Collections.emptyList());

        assertNotNull(commonService.getTimeseries("asset-123", start, end));

        Mockito.verify(timeseriesRepository).findByAssetIdAndBusinessDateBetweenOrderByVersionDesc("asset-123", end, start);
    }

    @Test
    void getTimeseries_ShouldReturnNull_WhenAssetDoesNotExist() {
        Mockito.when(assetRepository.findById("invalid-id")).thenReturn(Optional.empty());

        var result = commonService.getTimeseries("invalid-id", LocalDate.now(), LocalDate.now());
        assertNull(result);
    }
}