package com.satish.tms.dto;

import com.satish.tms.entity.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingDTO {
    
    private UUID bookingId;
    private UUID loadId;
    private UUID bidId;
    private UUID transporterId;
    private Integer allocatedTrucks;
    private Double finalRate;
    private BookingStatus status;
    private LocalDateTime bookedAt;
}