package com.fatia.inventoryservice.services;

import com.fatia.inventoryservice.exceptions.InsufficientQuantityException;
import com.fatia.inventoryservice.exceptions.InvalidStatusException;
import com.fatia.inventoryservice.exceptions.NotFoundException;
import com.fatia.inventoryservice.exceptions.SKUInUseException;
import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import com.fatia.inventoryservice.inventoryentities.SKUStatus;
import com.fatia.inventoryservice.inverntoryrepositories.SKURepository;
import com.fatia.inventoryservice.inverntoryrepositories.ShipmentItemRepository;
import com.fatia.inventoryservice.models.SKUModel;
import com.fatia.inventoryservice.requests.AddSKURequest;
import com.fatia.inventoryservice.requests.ChangeSKURequest;
import com.fatia.inventoryservice.requests.ChangeSKUStatusRequest;
import com.fatia.inventoryservice.requests.GenerateSKUCodeRequest;
import com.fatia.inventoryservice.warehouseentities.ShelfEntity;
import com.fatia.inventoryservice.warehouserepositories.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SKUService {

    private final SKURepository skuRepository;

    private final ShipmentItemRepository shipmentItemRepository;

    private final ShelfRepository shelfRepository;

    //For generating SKU code
    private String extractQuotedName(String name) {
        Pattern pattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String quoted = matcher.group(1);
            return quoted.replaceAll("(?i)[aeiou]", "").toUpperCase();
        }
        return "NONAME";
    }

    private String generateShortInfoBlock(String name) {
        // Remove quoted part
        String unquoted = name.replaceAll("\"([^\"]+)\"", "");
        // Remove content part after indicator (like "pack", "box")
        String mainPart = unquoted.replaceAll("\\b(pack|box|item|container)\\b.*", "");
        String[] words = mainPart.trim().split("\\s+");

        List<String> processed = new ArrayList<>();
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!word.isEmpty()) {
                String noVowels = word.replaceAll("[aeiou]", "");
                processed.add(noVowels.toUpperCase());
            }
        }

        return String.join("_", processed);
    }

    private String extractContentAfterIndicator(String name) {
        Pattern pattern = Pattern.compile("\\b(pack|box|item|container)\\b\\s*(\\S+)");
        Matcher matcher = pattern.matcher(name.toLowerCase());
        if (matcher.find()) {
            return matcher.group(2); // e.g., 0.25x9
        }
        return "CNT";
    }

    //Status validation
    private boolean isValidSKUStatus(String name) {
        return Arrays.stream(SKUStatus.values()).noneMatch(
                status -> status.name().equals(name));
    }

    public SKUModel getSKUById(Long id) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        return SKUModel.toModel(skuEntity.get());
    }

    public SKUModel getSKUByCode(String code) {
        Optional<SKUEntity> skuEntity = skuRepository.getSKUEntityByCode(code);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by code: " + code);
        }

        return SKUModel.toModel(skuEntity.get());
    }

    public List<SKUModel> getAllSKUs() {
        List<SKUEntity> entities = skuRepository.findAll();

        List<SKUModel> skuModels = new ArrayList<>();
        for (SKUEntity entity : entities) {
            skuModels.add(SKUModel.toModel(entity));
        }

        return skuModels;
    }

    public SKUModel addSKU(AddSKURequest request) {
        SKUEntity entity = SKUEntity
                .builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .status(SKUStatus.CREATED)
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .quantity(request.getQuantity())
                .storageConditions(request.getStorageConditions())
                .build();

        LocalDateTime date = LocalDateTime.now();

        entity.setCreatedAt(date);

        int batchCount = skuRepository.countByCode(request.getCode());

        String batch;
        if (batchCount == 0) {
            batch = "001";
        } else {
            batch = String.format("%03d", batchCount);
        }
        entity.setBatch(batch);

        skuRepository.save(entity);

        //TODO add function to send action to log service

        return SKUModel.toModel(entity);
    }

    public String generateSKUCode(GenerateSKUCodeRequest request) {
        String name = request.getName();
        String title = extractQuotedName(name);
        String shortInfo = generateShortInfoBlock(name);
        String content = extractContentAfterIndicator(name);

        //TODO find code for similar SKU name and compare it

        return title + "-" + shortInfo + "-" + content;
    }

    public void changeSKU(Long id, ChangeSKURequest request) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        SKUEntity entity = skuEntity.get();
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        entity.setLength(request.getLength());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setQuantity(request.getQuantity());
        entity.setStorageConditions(request.getStorageConditions());

        skuRepository.saveAndFlush(entity);

        //TODO add function to send action to log service
    }

    public void deleteSKUById(Long id) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        if (shipmentItemRepository.isSkuUsedInShipment(id)) {
            throw new SKUInUseException("SKU is used by another shipment");
        }

        skuRepository.deleteById(id);

        //TODO add function to send action to log service
    }

    public void increaseQuantity(Long id, Integer quantity) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        SKUEntity entity = skuEntity.get();
        entity.setQuantity(entity.getQuantity() + quantity);

        skuRepository.saveAndFlush(entity);
    }

    public void reduceQuantity(Long id, Integer quantity) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        SKUEntity entity = skuEntity.get();
        if (entity.getQuantity() < quantity) {
            throw new InsufficientQuantityException("Quantity exceeds the entity quantity");
        }
        entity.setQuantity(entity.getQuantity() - quantity);

        skuRepository.saveAndFlush(entity);
    }

    public void changeStatus(Long id, ChangeSKUStatusRequest request) {
        Optional<SKUEntity> optionalSKUEntity = skuRepository.findById(id);
        if (optionalSKUEntity.isEmpty()) {
            throw new NotFoundException("SKU not found by ID: " + id);
        }

        if (!isValidSKUStatus(request.getStatus())) {
            throw new InvalidStatusException("Status " + request.getStatus() + " is not valid ");
        }

        SKUEntity skuEntity = optionalSKUEntity.get();
        skuEntity.setStatus(SKUStatus.valueOf(request.getStatus()));

        //TODO add function to send action to log service
    }

    public SKUModel getSKUOnShelf(Long shelfId) {
        Optional<ShelfEntity> optionalShelfEntity = shelfRepository.findById(shelfId);
        if (optionalShelfEntity.isEmpty()) {
            throw new NotFoundException("Shelf not found by ID: " + shelfId);
        }

        Optional<SKUEntity> optionalSKUEntity = skuRepository.findSKUEntityByShelfId(shelfId);
        if (optionalSKUEntity.isEmpty()) {
            throw new NotFoundException("SKU not found on shelf with ID: " + shelfId);
        }

        SKUEntity skuEntity = optionalSKUEntity.get();
        return SKUModel.toModel(skuEntity);
    }

    public void setShelf(Long skuId, Long shelfId) {
        Optional<ShelfEntity> optionalShelfEntity = shelfRepository.findById(shelfId);
        if (optionalShelfEntity.isEmpty()) {
            throw new NotFoundException("Shelf not found by ID: " + shelfId);
        }

        Optional<SKUEntity> optionalSKUEntity = skuRepository.findById(skuId);
        if (optionalSKUEntity.isEmpty()) {
            throw new NotFoundException("SKU not found with ID: " + skuId);
        }

        SKUEntity skuEntity = optionalSKUEntity.get();
        skuEntity.setShelfId(shelfId);

        //TODO request for updateing shelf data in warehouse service

        skuRepository.saveAndFlush(skuEntity);
    }
}
