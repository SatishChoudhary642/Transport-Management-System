package com.satish.tms.service;

import com.satish.tms.dto.TransporterDTO;
import com.satish.tms.entity.Transporter;
import com.satish.tms.entity.TransporterTruck;
import com.satish.tms.exception.ResourceNotFoundException;
import com.satish.tms.repository.TransporterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransporterService {

    private final TransporterRepository transporterRepository;

    // 1. Register a new Transporter
    @Transactional // Ensures Parent + Child (Trucks) are saved together or not at all
    public TransporterDTO registerTransporter(TransporterDTO dto) {
        Transporter transporter = new Transporter();
        transporter.setCompanyName(dto.getCompanyName());
        transporter.setRating(dto.getRating() != null ? dto.getRating() : 0.0);

        // Handle the Trucks (The tricky part)
        if (dto.getAvailableTrucks() != null) {
            for (TransporterDTO.TruckDTO truckDTO : dto.getAvailableTrucks()) {
                TransporterTruck truck = new TransporterTruck();
                truck.setTruckType(truckDTO.getTruckType());
                truck.setTruckCount(truckDTO.getCount());
                
                // Use the helper method we wrote in the Entity to link them!
                transporter.addTruck(truck);
            }
        }

        Transporter saved = transporterRepository.save(transporter);
        return mapToDTO(saved);
    }

    // 2. Get Transporter by ID
    public TransporterDTO getTransporterById(UUID id) {
        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found: " + id));
        return mapToDTO(transporter);
    }

    // 3. Update Trucks (Requirement: PUT /transporter/{id}/trucks)
    @Transactional
    public TransporterDTO updateTrucks(UUID transporterId, java.util.List<TransporterDTO.TruckDTO> truckDTOs) {
        Transporter transporter = transporterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // Clear old trucks (Hibernate will delete these from DB because of orphanRemoval=true)
        transporter.getAvailableTrucks().clear();

        // Add new trucks
        for (TransporterDTO.TruckDTO truckDTO : truckDTOs) {
            TransporterTruck truck = new TransporterTruck();
            truck.setTruckType(truckDTO.getTruckType());
            truck.setTruckCount(truckDTO.getCount());
            transporter.addTruck(truck);
        }

        Transporter saved = transporterRepository.save(transporter);
        return mapToDTO(saved);
    }

    // Helper: Map Entity -> DTO
    private TransporterDTO mapToDTO(Transporter entity) {
        TransporterDTO dto = new TransporterDTO();
        dto.setTransporterId(entity.getTransporterId());
        dto.setCompanyName(entity.getCompanyName());
        dto.setRating(entity.getRating());

        // Convert List<TransporterTruck> -> List<TruckDTO>
        if (entity.getAvailableTrucks() != null) {
            dto.setAvailableTrucks(entity.getAvailableTrucks().stream().map(truck -> {
                TransporterDTO.TruckDTO tDto = new TransporterDTO.TruckDTO();
                tDto.setTruckType(truck.getTruckType());
                tDto.setCount(truck.getTruckCount());
                return tDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}