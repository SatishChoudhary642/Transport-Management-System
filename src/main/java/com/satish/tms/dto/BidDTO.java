package com.satish.tms.dto;

import com.satish.tms.entity.enums.BidStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BidDTO {
    
    private UUID bidId;

    @NotNull(message = "Load ID is required")
    private UUID loadId;

    @NotNull(message = "Transporter ID is required")
    private UUID transporterId;

    @NotNull(message = "Proposed rate is required")
    private Double proposedRate;

    @NotNull(message = "Number of trucks offered is required")
    private Integer trucksOffered;

    private BidStatus status;
    private LocalDateTime submittedAt;
}