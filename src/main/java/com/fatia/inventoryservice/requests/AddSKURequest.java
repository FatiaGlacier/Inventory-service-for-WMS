package com.fatia.inventoryservice.requests;

import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
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
