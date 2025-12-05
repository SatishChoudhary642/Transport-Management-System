package com.satish.tms.service;

import com.satish.tms.*;
import com.satish.tms.dto.LoadDTO;
import com.satish.tms.entity.Load;
import com.satish.tms.entity.enums.LoadStatus;
import com.satish.tms.exception.ResourceNotFoundException;
import com.satish.tms.repository.LoadRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor 
public class LoadService {

    private final LoadRepository loadRepository;

    // 1. Create Load
    public LoadDTO createLoad(LoadDTO loadDTO) {
        // Convert DTO to Entity
        Load load = new Load();
        load.setShipperId(loadDTO.getShipperId());
        load.setLoadingCity(loadDTO.getLoadingCity());
        load.setUnloadingCity(loadDTO.getUnloadingCity());
        load.setLoadingDate(loadDTO.getLoadingDate());
        load.setProductType(loadDTO.getProductType());
        load.setWeight(loadDTO.getWeight());
        load.setWeightUnit(loadDTO.getWeightUnit());
        load.setTruckType(loadDTO.getTruckType());
        load.setNoOfTrucks(loadDTO.getNoOfTrucks());
        
        // Business Rule: Initial status must be POSTED
        load.setStatus(LoadStatus.POSTED);

        // Save to DB
        Load savedLoad = loadRepository.save(load);

        // Convert back to DTO to return to controller
        return mapToDTO(savedLoad);
    }

    // 2. Get All Loads (With Filters and Pagination)
    public Page<LoadDTO> getAllLoads(String shipperId, LoadStatus status, int page, int size) {
        // Create Pageable object (Page 0, Size 10, Sort by Date Created DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by("datePosted").descending());

        Page<Load> loadPage;

        // Logic to choose the correct Repository method based on filters
        if (shipperId != null && status != null) {
            loadPage = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            loadPage = loadRepository.findByShipperId(shipperId, pageable);
        } else if (status != null) {
            loadPage = loadRepository.findByStatus(status, pageable);
        } else {
            loadPage = loadRepository.findAll(pageable);
        }

        // Convert the Page of Entities to a Page of DTOs
        return loadPage.map(this::mapToDTO);
    }

    // 3. Get Single Load by ID
    public LoadDTO getLoadById(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
        return mapToDTO(load);
    }

    // Helper Method: Convert Entity -> DTO
    private LoadDTO mapToDTO(Load load) {
        LoadDTO dto = new LoadDTO();
        dto.setLoadId(load.getLoadId());
        dto.setShipperId(load.getShipperId());
        dto.setLoadingCity(load.getLoadingCity());
        dto.setUnloadingCity(load.getUnloadingCity());
        dto.setLoadingDate(load.getLoadingDate());
        dto.setProductType(load.getProductType());
        dto.setWeight(load.getWeight());
        dto.setWeightUnit(load.getWeightUnit());
        dto.setTruckType(load.getTruckType());
        dto.setNoOfTrucks(load.getNoOfTrucks());
        dto.setStatus(load.getStatus());
        dto.setDatePosted(load.getDatePosted());
        return dto;
    }
}