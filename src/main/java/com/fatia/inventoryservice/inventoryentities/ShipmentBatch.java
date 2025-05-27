package com.fatia.inventoryservice.inventoryentities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shipment_batches")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ShipmentBatch {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private ShipmentStatus status;

    @OneToMany(
            mappedBy = "shipmentBatch",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ShipmentItem> items;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private Long shelfId;

    @Column(nullable = false)
    private String destinationPoint;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deliveredAt;
}
