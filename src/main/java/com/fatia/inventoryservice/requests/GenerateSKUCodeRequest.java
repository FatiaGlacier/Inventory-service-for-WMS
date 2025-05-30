package com.fatia.inventoryservice.requests;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateSKUCodeRequest {
    private String name;
}
