package com.satish.tms.repository;

import com.satish.tms.entity.TransporterTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransporterTruckRepository extends JpaRepository<TransporterTruck, UUID> {
}