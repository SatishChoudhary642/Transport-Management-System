package com.satish.tms.repository;

import com.satish.tms.entity.Load;
import com.satish.tms.entity.enums.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    
    // Finds loads by ShipperId AND Status
    // SELECT * FROM loads WHERE shipper_id = ? AND status = ?
    Page<Load> findByShipperIdAndStatus(String shipperId, LoadStatus status, Pageable pageable);

    // Finds loads just by Status
    Page<Load> findByStatus(LoadStatus status, Pageable pageable);

    // Finds loads just by ShipperId
    Page<Load> findByShipperId(String shipperId, Pageable pageable);
}