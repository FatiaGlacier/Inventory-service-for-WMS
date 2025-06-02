package com.fatia.inventoryservice.services;

import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentBatchEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentStatus;
import com.fatia.inventoryservice.inverntoryrepositories.SKURepository;
import com.fatia.inventoryservice.inverntoryrepositories.ShipmentBatchRepository;
import com.fatia.inventoryservice.models.ShipmentBatchModel;
import com.fatia.inventoryservice.requests.AddShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ChangeShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ShipmentItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ShipmentBatchService {

    private final ShipmentBatchRepository shipmentBatchRepository;

    private final SKURepository skuRepository;

    private final SKUService skuService;

    public List<ShipmentBatchModel> getAll() {
        List<ShipmentBatchEntity> shipmentBatchEntities = shipmentBatchRepository.findAll();

        List<ShipmentBatchModel> shipmentBatchModels = new ArrayList<>();
        for (ShipmentBatchEntity entity : shipmentBatchEntities) {
            shipmentBatchModels.add(ShipmentBatchModel.toModel(entity));
        }

        return shipmentBatchModels;
    }

    public ShipmentBatchModel getShipmentBatchById(Long id) {
        Optional<ShipmentBatchEntity> entity = shipmentBatchRepository.findById(id);
        if (entity.isEmpty()) {
            throw new RuntimeException("ShipmentBatch with id " + id + " not found");
        }

        return ShipmentBatchModel.toModel(entity.get());
    }

    public ShipmentBatchModel addShipmentBatch(AddShipmentBatchRequest request) {
        ShipmentBatchEntity entity = new ShipmentBatchEntity();

        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        int count = shipmentBatchRepository.countByTrackingNumberLike(datePart + "%");

        String sequence = String.format("%04d", count + 1);
        String trackingNumber = datePart + sequence;

        entity.setTrackingNumber(trackingNumber);
        entity.setDestinationPoint(request.getDestinationPoint());
        entity.setStatus(ShipmentStatus.CREATED);
        entity.setCreatedAt(LocalDateTime.now());

        List<ShipmentItemEntity> items = new ArrayList<>();

        for (ShipmentItemRequest item : request.getItems()) {
            SKUEntity skuEntity = skuRepository
                    .findById(item.getSkuId())
                    .orElseThrow(() -> new RuntimeException("SKU with id " + item.getSkuId() + " not found"));

            ShipmentItemEntity itemEntity = new ShipmentItemEntity();
            itemEntity.setShipmentBatch(entity);
            itemEntity.setSku(skuEntity);
            itemEntity.setQuantity(item.getQuantity());

            items.add(itemEntity);
            skuService.reduceQuantity(item.getSkuId(), item.getQuantity());
        }

        entity.setItems(items);
        shipmentBatchRepository.saveAndFlush(entity);

        return ShipmentBatchModel.toModel(entity);
    }

    public void changeShipmentBatch(ChangeShipmentBatchRequest request) {
        Optional<ShipmentBatchEntity> optionalShipmentBatchEntity = shipmentBatchRepository.findById(request.getId());
        if (optionalShipmentBatchEntity.isEmpty()) {
            throw new RuntimeException("ShipmentBatch with id " + request.getId() + " not found");
        }

        //Creating map of new items
        ShipmentBatchEntity batchEntity = optionalShipmentBatchEntity.get();
        Map<Long, ShipmentItemRequest> newItemMap = new HashMap<>();
        for (ShipmentItemRequest item : request.getItems()) {
            newItemMap.put(item.getSkuId(), item);
        }

        //Comparing old and new items list
        Iterator<ShipmentItemEntity> iterator = batchEntity.getItems().iterator();
        while (iterator.hasNext()) {
            ShipmentItemEntity oldItem = iterator.next();
            Long skuID = oldItem.getSku().getId();
            ShipmentItemRequest newItem = newItemMap.get(skuID);
            if (newItem == null) {
                skuService.increaseQuantity(skuID, oldItem.getQuantity());
                iterator.remove();
            } else {
                if (!Objects.equals(oldItem.getQuantity(), newItem.getQuantity())) {
                    int diff = newItem.getQuantity() - oldItem.getQuantity();
                    if (diff > 0) {
                        skuService.reduceQuantity(skuID, diff);
                    } else {
                        skuService.increaseQuantity(skuID, -diff);
                    }
                    oldItem.setQuantity(newItem.getQuantity());
                }
                newItemMap.remove(skuID);
            }
        }

        //Adding new elements
        for (ShipmentItemRequest newItem : newItemMap.values()) {
            SKUEntity skuEntity = skuRepository
                    .findById(newItem.getSkuId())
                    .orElseThrow(() -> new RuntimeException("SKU with id " + newItem.getSkuId() + " not found"));

            ShipmentItemEntity itemEntity = new ShipmentItemEntity();
            itemEntity.setShipmentBatch(batchEntity);
            itemEntity.setSku(skuEntity);
            itemEntity.setQuantity(newItem.getQuantity());

            batchEntity.getItems().add(itemEntity);
            skuService.reduceQuantity(skuEntity.getId(), newItem.getQuantity());
        }

        shipmentBatchRepository.saveAndFlush(batchEntity);
    }
}
