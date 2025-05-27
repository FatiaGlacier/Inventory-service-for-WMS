package com.fatia.inventoryservice.inventoryentities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment_items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ShipmentItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "shipment_batch_id")
    private ShipmentBatch shipmentBatch;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(name = "sku_id")
    private SKU storageKeepingUnit;

    @Column(nullable = false)
    private Long quantity;
}
