package com.satish.tms.controller;

import com.satish.tms.dto.*;
import com.satish.tms.entity.enums.*;
import com.satish.tms.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;
    private final BidService bidService; 

    // POST /load (Create a load) 
    @PostMapping
    public ResponseEntity<LoadDTO> createLoad(@Valid @RequestBody LoadDTO loadDTO) {
        return new ResponseEntity<>(loadService.createLoad(loadDTO), HttpStatus.CREATED);
    }

    // GET /load (List with filters & pagination) 
    @GetMapping
    public ResponseEntity<Page<LoadDTO>> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(loadService.getAllLoads(shipperId, status, page, size));
    }

    //  GET /load/{loadId} (Get details) 
    @GetMapping("/{loadId}")
    public ResponseEntity<LoadDTO> getLoadById(@PathVariable UUID loadId) {
        return ResponseEntity.ok(loadService.getLoadById(loadId));
    }

    //  PATCH /load/{loadId}/cancel (Cancel load) 
    @PatchMapping("/{loadId}/cancel")
    public ResponseEntity<Void> cancelLoad(@PathVariable UUID loadId) {
        loadService.cancelLoad(loadId);
        return ResponseEntity.noContent().build();
    }

    // GET /load/{loadId}/best-bids (Smart Sorting) 
    @GetMapping("/{loadId}/best-bids")
    public ResponseEntity<List<BidDTO>> getBestBids(@PathVariable UUID loadId) {
        return ResponseEntity.ok(bidService.getBestBidsForLoad(loadId));
    }
}