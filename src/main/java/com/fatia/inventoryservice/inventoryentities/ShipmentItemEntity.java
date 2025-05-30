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
public class ShipmentItemEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(name = "shipment_batch_id")
    private ShipmentBatchEntity shipmentBatch;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(name = "sku_id")
    private SKUEntity storageKeepingUnit;

    @Column(nullable = false)
    private Long quantity;
}
