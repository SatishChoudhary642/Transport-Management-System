package com.satish.tms.entity;

import com.satish.tms.entity.enums.*;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "loads", 
    indexes = {
        @Index(name = "idx_load_shipper", columnList = "shipperId"),
        @Index(name = "idx_load_status", columnList = "status") 
    }
)
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loadId;
    
    @Column(nullable = false)
    private String shipperId;

    @Column(nullable = false)
    private String loadingCity;

    @Column(nullable = false)
    private String unloadingCity;

    @Column(nullable = false)
    private String productType;

    @Column(nullable = false)
    private String truckType;

    @Column(nullable = false)
    private int noOfTrucks;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private LocalDateTime loadingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeightUnit weightUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoadStatus status; 

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime datePosted;

    @UpdateTimestamp
    private LocalDateTime lastModified;

    @Version
    private Long version;

    // Relationships
    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();
}