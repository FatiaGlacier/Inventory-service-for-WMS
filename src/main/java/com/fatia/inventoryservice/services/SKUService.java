package com.fatia.inventoryservice.services;

import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import com.fatia.inventoryservice.inventoryentities.SKUStatus;
import com.fatia.inventoryservice.inverntoryrepositories.SKURepository;
import com.fatia.inventoryservice.models.SKUModel;
import com.fatia.inventoryservice.requests.AddSKURequest;
import com.fatia.inventoryservice.requests.ChangeSKURequest;
import com.fatia.inventoryservice.requests.GenerateSKUCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SKUService {

    private final SKURepository skuRepository;

    public SKUModel getSKUById(Long id) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new RuntimeException("SKU not found by ID: " + id);//TODO exception
        }

        return SKUModel.toModel(skuEntity.get());
    }

    public SKUModel getSKUByCode(String code) {
        Optional<SKUEntity> skuEntity = skuRepository.getSKUEntityByCode(code);
        if (skuEntity.isEmpty()) {
            return null; //TODO exception
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

        //TODO set shelf

        skuRepository.save(entity);

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

    public void changeSKU(ChangeSKURequest request) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(request.getId());
        if (skuEntity.isEmpty()) {
            throw new RuntimeException("SKU not found by ID: " + request.getId());//TODO exception
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
    }

    public void deleteSKUById(Long id) {
        Optional<SKUEntity> skuEntity = skuRepository.findById(id);
        if (skuEntity.isEmpty()) {
            throw new RuntimeException("SKU not found by ID: " + id);//TODO exception
        }

        skuRepository.deleteById(id);
    }
}
