package com.satish.tms.controller;

import com.satish.tms.dto.*;
import com.satish.tms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //  POST /booking (Accept bid -> Create Booking)
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody Map<String, UUID> request) {
        // We expect JSON: { "bidId": "..." }
        UUID bidId = request.get("bidId");
        return new ResponseEntity<>(bookingService.acceptBid(bidId), HttpStatus.CREATED);
    }

    //  GET /booking/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable UUID bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    //  PATCH /booking/{bookingId}/cancel
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}