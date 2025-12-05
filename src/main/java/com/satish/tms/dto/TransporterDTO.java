package com.satish.tms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class TransporterDTO {
    
    private UUID transporterId;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private Double rating;

    private List<TruckDTO> availableTrucks;

    @Data
    public static class TruckDTO {
        @NotBlank
        private String truckType;
        private int count;
    }
}