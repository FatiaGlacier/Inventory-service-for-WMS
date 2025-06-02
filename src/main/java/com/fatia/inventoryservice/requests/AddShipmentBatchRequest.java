package com.fatia.inventoryservice.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddShipmentBatchRequest {
    private List<ShipmentItemRequest> items;
    private String destinationPoint;
}
