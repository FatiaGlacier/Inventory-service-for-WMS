package com.fatia.inventoryservice.controllers;

import com.fatia.inventoryservice.models.SKUModel;
import com.fatia.inventoryservice.requests.AddSKURequest;
import com.fatia.inventoryservice.requests.ChangeSKURequest;
import com.fatia.inventoryservice.requests.ChangeSKUStatusRequest;
import com.fatia.inventoryservice.requests.GenerateSKUCodeRequest;
import com.fatia.inventoryservice.services.SKUService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/storage-keeping-unit")
@RequiredArgsConstructor
public class SKUController {

    private final SKUService skuService;

    @GetMapping("/get-all")
    public ResponseEntity<List<SKUModel>> getAllSKUs() {
        return ResponseEntity.ok(skuService.getAllSKUs());
    }

    @GetMapping("/get-sku/{id}")
    public ResponseEntity<SKUModel> getSKUById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(skuService.getSKUById(id));
    }

    @PostMapping("/add-sku")
    public ResponseEntity<SKUModel> addSKU(
            @RequestBody AddSKURequest request
    ) {
        return ResponseEntity.ok(skuService.addSKU(request));
    }

    @PostMapping("/generate-code")
    public ResponseEntity<String> generateSKUCode(
            @RequestBody GenerateSKUCodeRequest request
    ) {
        return ResponseEntity.ok(skuService.generateSKUCode(request));
    }

    @PutMapping("/change-sku")
    public ResponseEntity<SKUModel> changeSKU(
            @RequestBody ChangeSKURequest request
    ) {
        skuService.changeSKU(request);
        return ResponseEntity.ok(skuService.getSKUById(request.getId()));
    }

    @DeleteMapping("/delete-sku/{id}")
    public ResponseEntity<Void> deleteSKU(@PathVariable Long id) {
        skuService.deleteSKUById(id);
        return ResponseEntity.ok().build();
    }

    //TODO change-status
    @PatchMapping("/change-status")
    public ResponseEntity<SKUModel> changeStatus(
            @RequestBody ChangeSKUStatusRequest request
    ) {
        skuService.changeStatus(request);
        return ResponseEntity.ok(skuService.getSKUById(request.getId()));
    }


    //TODO find SKU on shelf by shelf id

    //TODO add set-shelf

}
