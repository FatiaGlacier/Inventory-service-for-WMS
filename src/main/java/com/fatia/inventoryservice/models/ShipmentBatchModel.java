package com.fatia.inventoryservice.models;

import com.fatia.inventoryservice.inventoryentities.ShipmentBatchEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentBatchModel {

    private Long id;

    private ShipmentStatus status;

    private List<ShipmentItemEntity> items;

    private String trackingNumber;

    private Long shelfId;

    private String destinationPoint;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deliveredAt;

    public static ShipmentBatchModel toModel(ShipmentBatchEntity shipmentBatchEntity) {
        return ShipmentBatchModel.builder()
                .id(shipmentBatchEntity.getId())
                .status(shipmentBatchEntity.getStatus())
                .items(shipmentBatchEntity.getItems())
                .shelfId(shipmentBatchEntity.getShelfId())
                .destinationPoint(shipmentBatchEntity.getDestinationPoint())
                .createdAt(shipmentBatchEntity.getCreatedAt())
                .updatedAt(shipmentBatchEntity.getUpdatedAt())
                .deliveredAt(shipmentBatchEntity.getDeliveredAt())
                .build();
    }
}
