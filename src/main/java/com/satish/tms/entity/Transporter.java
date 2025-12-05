package com.satish.tms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transporters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transporter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transporterId; 

    @Column(nullable = false)
    private String companyName; 

    @Column(nullable = false)
    private Double rating; 

    // One Transporter has Many Truck Types (One-to-Many)
    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporterTruck> availableTrucks = new ArrayList<>(); // 

    // Helper method to add trucks easily 
    public void addTruck(TransporterTruck truck) {
        availableTrucks.add(truck);
        truck.setTransporter(this);
    }
}