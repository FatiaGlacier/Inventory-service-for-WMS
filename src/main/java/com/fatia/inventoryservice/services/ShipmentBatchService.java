package com.fatia.inventoryservice.services;

import com.fatia.inventoryservice.exceptions.HasStatusException;
import com.fatia.inventoryservice.exceptions.InvalidStatusException;
import com.fatia.inventoryservice.exceptions.NotFoundException;
import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentBatchEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import com.fatia.inventoryservice.inventoryentities.ShipmentStatus;
import com.fatia.inventoryservice.inverntoryrepositories.SKURepository;
import com.fatia.inventoryservice.inverntoryrepositories.ShipmentBatchRepository;
import com.fatia.inventoryservice.models.ShipmentBatchModel;
import com.fatia.inventoryservice.requests.AddShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ChangeShipmentBatchRequest;
import com.fatia.inventoryservice.requests.ChangeShipmentStatusRequest;
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
            throw new NotFoundException("ShipmentBatch not found by ID: " + id);
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
                    .orElseThrow(() -> new NotFoundException("SKU not found with id " + item.getSkuId()));

            ShipmentItemEntity itemEntity = new ShipmentItemEntity();
            itemEntity.setShipmentBatch(entity);
            itemEntity.setSku(skuEntity);
            itemEntity.setQuantity(item.getQuantity());

            items.add(itemEntity);
            skuService.reduceQuantity(item.getSkuId(), item.getQuantity());
        }

        entity.setItems(items);
        shipmentBatchRepository.saveAndFlush(entity);

        //TODO add function to send action to log service

        return ShipmentBatchModel.toModel(entity);
    }

    public void changeShipmentBatch(ChangeShipmentBatchRequest request) {
        Optional<ShipmentBatchEntity> optionalShipmentBatchEntity = shipmentBatchRepository.findById(request.getId());
        if (optionalShipmentBatchEntity.isEmpty()) {
            throw new NotFoundException("ShipmentBatch not found with id " + request.getId());
        }

        ShipmentBatchEntity batchEntity = optionalShipmentBatchEntity.get();

        if (batchEntity.getStatus() == ShipmentStatus.PICKING
                || batchEntity.getStatus() == ShipmentStatus.DELIVERED
                || batchEntity.getStatus() == ShipmentStatus.SHIPPED) {
            throw new HasStatusException("ShipmentBatch with id " + request.getId() + " has status " + batchEntity.getStatus());
        }

        //Creating map of new items
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
                    .orElseThrow(() -> new NotFoundException("SKU not found with id " + newItem.getSkuId()));

            ShipmentItemEntity itemEntity = new ShipmentItemEntity();
            itemEntity.setShipmentBatch(batchEntity);
            itemEntity.setSku(skuEntity);
            itemEntity.setQuantity(newItem.getQuantity());

            batchEntity.getItems().add(itemEntity);
            skuService.reduceQuantity(skuEntity.getId(), newItem.getQuantity());
        }

        //TODO add function to send action to log service

        shipmentBatchRepository.saveAndFlush(batchEntity);
    }

    public void changeStatus(ChangeShipmentStatusRequest request) {
        Optional<ShipmentBatchEntity> optionalShipmentBatchEntity = shipmentBatchRepository.findById(request.getId());
        if (optionalShipmentBatchEntity.isEmpty()) {
            throw new NotFoundException("ShipmentBatch not found with id " + request.getId());
        }

        if (!isValidShipmentStatus(request.getStatus())) {
            throw new InvalidStatusException("Status " + request.getStatus() + " is not valid ");
        }

        ShipmentBatchEntity batchEntity = optionalShipmentBatchEntity.get();
        batchEntity.setStatus(ShipmentStatus.valueOf(request.getStatus()));

        //TODO add function to send action to log service
    }

    public void deleteShipmentBatch(Long id) {
        Optional<ShipmentBatchEntity> optionalShipmentBatchEntity = shipmentBatchRepository.findById(id);
        if (optionalShipmentBatchEntity.isEmpty()) {
            throw new NotFoundException("ShipmentBatch with id " + id + " not found");
        }

        ShipmentBatchEntity batchEntity = optionalShipmentBatchEntity.get();

        //TODO returning SKUs if status picking/delivered
        //Maybe separate function from vehicle/warehouse service which changes count on warehouse from commands from vehicle when it arrives to the place

        //Temporary variant
        if (batchEntity.getStatus() == ShipmentStatus.CREATED
                || batchEntity.getStatus() == ShipmentStatus.UPDATED) {
            for (ShipmentItemEntity item : batchEntity.getItems()) {
                skuService.increaseQuantity(item.getSku().getId(), item.getQuantity());
            }
        }

        //TODO add function to save deleted object

        //TODO add function to send action to log service

        shipmentBatchRepository.delete(batchEntity);

    }

    private boolean isValidShipmentStatus(String name) {
        return Arrays.stream(ShipmentStatus.values()).noneMatch(
                status -> status.name().equals(name));
    }
}
