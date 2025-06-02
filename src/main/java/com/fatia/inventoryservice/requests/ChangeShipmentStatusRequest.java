package com.fatia.inventoryservice.requests;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeShipmentStatusRequest {
    Long id;
    String status;
}
