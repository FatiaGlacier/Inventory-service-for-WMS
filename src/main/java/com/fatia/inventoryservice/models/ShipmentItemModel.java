package com.fatia.inventoryservice.models;

import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentItemModel {
    private Long skuId;
    private String skuName;
    private String skuCode;
    private int quantity;

    public static ShipmentItemModel toModel(ShipmentItemEntity entity) {
        return ShipmentItemModel
                .builder()
                .skuId(entity.getSku().getId())
                .skuName(entity.getSku().getName())
                .skuCode(entity.getSku().getCode())
                .quantity(entity.getQuantity())
                .build();

    }
}
