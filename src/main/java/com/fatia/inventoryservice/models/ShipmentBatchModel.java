package com.fatia.inventoryservice.models;

import com.fatia.inventoryservice.inventoryentities.ShipmentBatchEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentBatchModel {

    private Long id;

    private ShipmentStatus status;

    private List<ShipmentItemModel> items;

    private String trackingNumber;

    private Long shelfId;

    private String destinationPoint;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deliveredAt;

    public static ShipmentBatchModel toModel(ShipmentBatchEntity shipmentBatchEntity) {

        ShipmentBatchModel model = new ShipmentBatchModel();
        model.setId(shipmentBatchEntity.getId());
        model.setStatus(shipmentBatchEntity.getStatus());
        model.setTrackingNumber(shipmentBatchEntity.getTrackingNumber());
        model.setCreatedAt(shipmentBatchEntity.getCreatedAt());
        model.setUpdatedAt(shipmentBatchEntity.getUpdatedAt());
        model.setDeliveredAt(shipmentBatchEntity.getDeliveredAt());
        model.setShelfId(shipmentBatchEntity.getShelfId());
        model.setDestinationPoint(shipmentBatchEntity.getDestinationPoint());

        for (ShipmentItemEntity item : shipmentBatchEntity.getItems()) {
            model.getItems().add(ShipmentItemModel.toModel(item));
        }

        return model;

    }
}
