package com.fatia.inventoryservice.models;

import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import com.fatia.inventoryservice.inventoryentities.SKUStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SKUModel {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String batch;

    private SKUStatus status;

    private int length;

    private int width;

    private int height;

    private Integer weight; // for one unit

    private Integer totalQuantity;

    private Integer reservedQuantity;

    private Integer availableQuantity;

    private Long shelfId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Map<String, String> storageConditions;

    public static SKUModel toModel(SKUEntity skuEntity) {

        return SKUModel.builder()
                .id(skuEntity.getId())
                .code(skuEntity.getCode())
                .name(skuEntity.getName())
                .description(skuEntity.getDescription())
                .batch(skuEntity.getBatch())
                .status(skuEntity.getStatus())
                .length(skuEntity.getLength())
                .width(skuEntity.getWidth())
                .height(skuEntity.getHeight())
                .weight(skuEntity.getWeight())
                .totalQuantity(skuEntity.getTotalQuantity())
                .reservedQuantity(skuEntity.getReservedQuantity())
                .availableQuantity(skuEntity.getAvailableQuantity())
                .shelfId(skuEntity.getShelfId())
                .createdAt(skuEntity.getCreatedAt())
                .updatedAt(skuEntity.getUpdatedAt())
                .storageConditions(skuEntity.getStorageConditions())
                .build();
    }
}
