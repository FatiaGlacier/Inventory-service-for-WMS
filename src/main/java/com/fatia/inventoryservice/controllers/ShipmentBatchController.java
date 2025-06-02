package com.fatia.inventoryservice.controllers;

import com.fatia.inventoryservice.models.ShipmentBatchModel;
import com.fatia.inventoryservice.requests.AddShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ChangeShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ChangeShipmentStatusRequest;
import com.fatia.inventoryservice.services.SKUService;
import com.fatia.inventoryservice.services.ShipmentBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/shipment-batch")
@RequiredArgsConstructor
public class ShipmentBatchController {

    private final ShipmentBatchService shipmentBatchService;

    private final SKUService skuService;

    @GetMapping("/get-all")
    public ResponseEntity<List<ShipmentBatchModel>> getAllShipmentBatches() {
        return ResponseEntity.ok(shipmentBatchService.getAll());
    }

    @GetMapping("/get-shipment-batch/{id}")
    public ResponseEntity<ShipmentBatchModel> getShipmentBatchById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(shipmentBatchService.getShipmentBatchById(id));
    }

    @PostMapping("/add-shipment-batch")
    public ResponseEntity<ShipmentBatchModel> addShipmentBatch(
            @RequestBody AddShipmentBatchRequest request
    ) {
        return ResponseEntity.ok(shipmentBatchService.addShipmentBatch(request));
    }

    @PutMapping("/change-shipment-batch")
    public ResponseEntity<ShipmentBatchModel> changeShipmentBatch(
            @RequestBody ChangeShipmentBatchRequest request
    ) {
        shipmentBatchService.changeShipmentBatch(request);
        return ResponseEntity.ok(shipmentBatchService.getShipmentBatchById(request.getId()));
    }

    @PatchMapping("/change-status")
    public ResponseEntity<ShipmentBatchModel> changeStatus(
            @RequestBody ChangeShipmentStatusRequest request
    ) {
        shipmentBatchService.changeStatus(request);
        return ResponseEntity.ok(shipmentBatchService.getShipmentBatchById(request.getId()));
    }
    
    @DeleteMapping("/delete-shipment-batch/{id}")
    public ResponseEntity<String> deleteShipmentBatch(
            @RequestParam Long id
    ) {
        shipmentBatchService.deleteShipmentBatch(id);
        return ResponseEntity.ok("Deleted shipment batch with id " + id);
    }

    //TODO set-shelf

    //TODO exceptions
}
