package com.fatia.inventoryservice.requests;

import com.fatia.inventoryservice.inventoryentities.SKUStatus;

import java.util.Map;

public class UpdateSKURequest {
    private String name;

    private String description;

    private SKUStatus status;

    private int length;

    private int width;

    private int height;

    private Integer quantity;

    private Long shelfId;

    private Map<String, String> storageConditions;
}
