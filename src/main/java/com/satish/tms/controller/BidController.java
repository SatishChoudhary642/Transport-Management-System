package com.satish.tms.controller;

import com.satish.tms.dto.*;
import com.satish.tms.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bid")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    // POST /bid (Submit a bid)
    @PostMapping
    public ResponseEntity<BidDTO> submitBid(@Valid @RequestBody BidDTO bidDTO) {
        return new ResponseEntity<>(bidService.submitBid(bidDTO), HttpStatus.CREATED);
    }

    // GET /bid (Get bids for a load)
    @GetMapping
    public ResponseEntity<List<BidDTO>> getBids(@RequestParam UUID loadId) {
        return ResponseEntity.ok(bidService.getBidsForLoad(loadId));
    }

    // PATCH /bid/{bidId}/reject (Reject a bid)
    @PatchMapping("/{bidId}/reject")
    public ResponseEntity<Void> rejectBid(@PathVariable UUID bidId) {
        bidService.rejectBid(bidId);
        return ResponseEntity.noContent().build();
    }
}