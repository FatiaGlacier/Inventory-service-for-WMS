package com.fatia.inventoryservice.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddSKURequest {
    private String name;

    private String code;

    private String description;

    private int length;

    private int width;

    private int height;

    private Integer quantity;

    private Map<String, String> storageConditions;
}
