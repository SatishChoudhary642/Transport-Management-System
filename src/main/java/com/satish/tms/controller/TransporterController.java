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
@RequestMapping("/transporter")
@RequiredArgsConstructor
public class TransporterController {

    private final TransporterService transporterService;

    //  POST /transporter (Register) 
    @PostMapping
    public ResponseEntity<TransporterDTO> registerTransporter(@Valid @RequestBody TransporterDTO dto) {
        return new ResponseEntity<>(transporterService.registerTransporter(dto), HttpStatus.CREATED);
    }

    // GET /transporter/{id} (Get Profile) 
    @GetMapping("/{transporterId}")
    public ResponseEntity<TransporterDTO> getTransporter(@PathVariable UUID transporterId) {
        return ResponseEntity.ok(transporterService.getTransporterById(transporterId));
    }

    // PUT /transporter/{id}/trucks (Update Capacity) 
    @PutMapping("/{transporterId}/trucks")
    public ResponseEntity<TransporterDTO> updateTrucks(
            @PathVariable UUID transporterId,
            @Valid @RequestBody List<TransporterDTO.TruckDTO> truckDTOs
    ) {
        return ResponseEntity.ok(transporterService.updateTrucks(transporterId, truckDTOs));
    }
}