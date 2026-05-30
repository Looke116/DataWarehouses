package org.example.backend.DTOs.TwelveData;

import lombok.Data;

import java.util.List;

@Data
public class TwelveDataDTO {
    private MetaDTO meta;
    private List<ValueDTO> values;
    private String status;
}
