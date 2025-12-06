package com.satish.tms.service;

import com.satish.tms.dto.BookingDTO;
import com.satish.tms.entity.*;
import com.satish.tms.entity.enums.BidStatus;
import com.satish.tms.entity.enums.BookingStatus;
import com.satish.tms.entity.enums.LoadStatus;
import com.satish.tms.exception.InsufficientCapacityException;
import com.satish.tms.exception.InvalidStatusTransitionException;
import com.satish.tms.exception.ResourceNotFoundException;
import com.satish.tms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LoadRepository loadRepository;
    private final BidRepository bidRepository;
    private final TransporterRepository transporterRepository;

    /**
     * Rule 3 & 4: Accept Bid -> Create Booking
     * This method handles the complex logic of deducting trucks and updating status.
     */
    @Transactional // Critical: If any part fails, roll back everything
    public BookingDTO acceptBid(UUID bidId) {
        // 1. Fetch the Bid
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        // 2. Fetch the Load & Transporter
        Load load = bid.getLoad();
        Transporter transporter = bid.getTransporter();

        // 3. Validation: Can we accept this bid?
        if (load.getStatus() == LoadStatus.CANCELLED || load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Load is already BOOKED or CANCELLED");
        }

        // 4. Rule 3: Calculate Remaining Trucks
        // (Total Trucks Needed) - (Trucks Already Booked)
        int alreadyBookedCount = load.getBids().stream()
                .filter(b -> b.getStatus() == BidStatus.ACCEPTED) // Only count accepted bids
                .mapToInt(Bid::getTrucksOffered)
                .sum();

        int needed = load.getNoOfTrucks() - alreadyBookedCount;
        
        if (needed <= 0) {
            throw new InvalidStatusTransitionException("Load is already fully allocated");
        }

        // If the bid offers more trucks than we need, we only take what we need?
        // Requirement implies strict matching, but let's assume we take the whole bid amount 
        // unless it exceeds the total limit.
        if (bid.getTrucksOffered() > needed) {
            throw new InsufficientCapacityException("Bid trucks (" + bid.getTrucksOffered() + 
                ") exceed remaining load requirement (" + needed + ")");
        }

        // 5. Deduct Trucks from Transporter (Rule 1)
        TransporterTruck truckRecord = transporter.getAvailableTrucks().stream()
                .filter(t -> t.getTruckType().equals(load.getTruckType()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Transporter truck record not found"));

        if (truckRecord.getTruckCount() < bid.getTrucksOffered()) {
            throw new InsufficientCapacityException("Transporter no longer has enough trucks available");
        }
        
        // Update Transporter Inventory
        truckRecord.setTruckCount(truckRecord.getTruckCount() - bid.getTrucksOffered());
        transporterRepository.save(transporter);

        // 6. Create the Booking
        Booking booking = new Booking();
        booking.setLoad(load);
        booking.setBid(bid);
        booking.setTransporter(transporter);
        booking.setAllocatedTrucks(bid.getTrucksOffered());
        booking.setFinalRate(bid.getProposedRate());
        booking.setStatus(BookingStatus.CONFIRMED);
        
        Booking savedBooking = bookingRepository.save(booking);

        // 7. Update Bid Status
        bid.setStatus(BidStatus.ACCEPTED);
        bidRepository.save(bid);

        // 8. Update Load Status (Rule 3)
        // If (newly allocated + old allocated) == Total, mark load as BOOKED
        if (alreadyBookedCount + bid.getTrucksOffered() >= load.getNoOfTrucks()) {
            load.setStatus(LoadStatus.BOOKED);
        }
        
        // SAVE LOAD -> This triggers the @Version check (Optimistic Locking)
        // If another user updated this load 1 millisecond ago, this line will crash 
        // with ObjectOptimisticLockingFailureException.
        loadRepository.save(load); 

        return mapToDTO(savedBooking);
    }

    public BookingDTO getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToDTO(booking);
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Booking is already cancelled");
        }

        // 1. Update Booking Status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // 2. Restore Trucks to Transporter (Rule 1)
        Transporter transporter = booking.getTransporter();
        Load load = booking.getLoad();

        TransporterTruck truckRecord = transporter.getAvailableTrucks().stream()
                .filter(t -> t.getTruckType().equals(load.getTruckType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Truck record mismatch"));

        truckRecord.setTruckCount(truckRecord.getTruckCount() + booking.getAllocatedTrucks());
        transporterRepository.save(transporter);

        // 3. Update Load Status (Optional logic: If load was BOOKED, make it OPEN_FOR_BIDS again?)
        // The requirements [cite: 60] say BOOKED -> CANCELLED, but for the Load itself, 
        // if a booking is cancelled, the Load might still be valid for other bids.
        // For simplicity/safety, we will flip the Load back to OPEN_FOR_BIDS if it was BOOKED.
        if (load.getStatus() == LoadStatus.BOOKED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }
    }

    // Helper
    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setLoadId(booking.getLoad().getLoadId());
        dto.setBidId(booking.getBid().getBidId());
        dto.setTransporterId(booking.getTransporter().getTransporterId());
        dto.setAllocatedTrucks(booking.getAllocatedTrucks());
        dto.setFinalRate(booking.getFinalRate());
        dto.setStatus(booking.getStatus());
        dto.setBookedAt(booking.getBookedAt());
        return dto;
    }
}