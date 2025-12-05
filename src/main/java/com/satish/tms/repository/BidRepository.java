package com.satish.tms.repository;

import com.satish.tms.entity.Bid;
import com.satish.tms.entity.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    // Find all bids for a specific Load 
    List<Bid> findByLoadLoadId(UUID loadId);

    // Find all bids made by a specific Transporter
    List<Bid> findByTransporterTransporterId(UUID transporterId);
    
    // Find bids by Load ID and Status 
    List<Bid> findByLoadLoadIdAndStatus(UUID loadId, BidStatus status);
}