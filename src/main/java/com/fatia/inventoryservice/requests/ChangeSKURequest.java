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
public class ChangeSKURequest {

    private String name;

    private String code;

    private String description;

    private int length;

    private int width;

    private int height;

    private int weight;

    private Integer totalQuantity;

    private Map<String, String> storageConditions;
}
