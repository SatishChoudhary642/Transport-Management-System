package com.satish.tms.dto;

import com.satish.tms.entity.enums.LoadStatus;
import com.satish.tms.entity.enums.WeightUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LoadDTO {
    
    private UUID loadId;

    @NotBlank(message = "Shipper ID is required")
    private String shipperId;

    @NotBlank(message = "Loading city is required")
    private String loadingCity;

    @NotBlank(message = "Unloading city is required")
    private String unloadingCity;

    @NotNull(message = "Loading date is required")
    private LocalDateTime loadingDate;

    @NotBlank(message = "Product type is required")
    private String productType;

    @NotNull(message = "Weight is required")
    private Double weight;

    @NotNull(message = "Weight unit is required")
    private WeightUnit weightUnit;

    @NotBlank(message = "Truck type is required")
    private String truckType;

    @NotNull(message = "Number of trucks is required")
    private Integer noOfTrucks;

    private LoadStatus status; // set by backend
    private LocalDateTime datePosted; // Set by backend
}