package com.satish.tms.service;

import com.satish.tms.dto.BidDTO;
import com.satish.tms.entity.Bid;
import com.satish.tms.entity.Load;
import com.satish.tms.entity.Transporter;
import com.satish.tms.entity.TransporterTruck;
import com.satish.tms.entity.enums.BidStatus;
import com.satish.tms.entity.enums.LoadStatus;
import com.satish.tms.exception.*;
import com.satish.tms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;

    // 1. Submit a Bid (Validation Heavy)
    @Transactional
    public BidDTO submitBid(BidDTO bidDTO) {
        // A. Fetch Entities
        Load load = loadRepository.findById(bidDTO.getLoadId())
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));
        
        Transporter transporter = transporterRepository.findById(bidDTO.getTransporterId())
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // B. Rule 2: Check Load Status 
        // Cannot bid if load is already BOOKED or CANCELLED
        if (load.getStatus() == LoadStatus.BOOKED || load.getStatus() == LoadStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot bid on a load that is " + load.getStatus());
        }

        // C. Rule 1: Capacity Validation 
        // Check if transporter actually has enough trucks of this type
        boolean hasCapacity = transporter.getAvailableTrucks().stream()
                .anyMatch(t -> t.getTruckType().equalsIgnoreCase(load.getTruckType()) 
                            && t.getTruckCount() >= bidDTO.getTrucksOffered());

        if (!hasCapacity) {
            throw new InsufficientCapacityException("Transporter does not have enough " + load.getTruckType() + " trucks.");
        }

        // D. Create Bid Entity
        Bid bid = new Bid();
        bid.setLoad(load);
        bid.setTransporter(transporter);
        bid.setProposedRate(bidDTO.getProposedRate());
        bid.setTrucksOffered(bidDTO.getTrucksOffered());
        bid.setStatus(BidStatus.PENDING); // Default status

        Bid savedBid = bidRepository.save(bid);

        // E. Rule 2: Auto-Update Load Status 
        // POSTED -> OPEN_FOR_BIDS (when first bid received)
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }

        return mapToDTO(savedBid);
    }

    // 2. Get Bids for a Load
    public List<BidDTO> getBidsForLoad(UUID loadId) {
        return bidRepository.findByLoadLoadId(loadId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 3. Rule 5: Best Bid Calculation
    public List<BidDTO> getBestBidsForLoad(UUID loadId) {
        List<Bid> bids = bidRepository.findByLoadLoadId(loadId);

        // Sort in Java using the formula
        return bids.stream()
                .sorted((b1, b2) -> {
                    double score1 = calculateScore(b1);
                    double score2 = calculateScore(b2);
                    // Descending order (Higher score is better)
                    return Double.compare(score2, score1);
                })
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper: The Math Formula
    private double calculateScore(Bid bid) {
        double rate = bid.getProposedRate();
        double rating = bid.getTransporter().getRating();
        
        // Formula: (1 / proposedRate) * 0.7 + (rating / 5) * 0.3
        return (1.0 / rate) * 0.7 + (rating / 5.0) * 0.3;
    }

    private BidDTO mapToDTO(Bid bid) {
        BidDTO dto = new BidDTO();
        dto.setBidId(bid.getBidId());
        dto.setLoadId(bid.getLoad().getLoadId());
        dto.setTransporterId(bid.getTransporter().getTransporterId());
        dto.setProposedRate(bid.getProposedRate());
        dto.setTrucksOffered(bid.getTrucksOffered());
        dto.setStatus(bid.getStatus());
        dto.setSubmittedAt(bid.getSubmittedAt());
        return dto;
    }
}