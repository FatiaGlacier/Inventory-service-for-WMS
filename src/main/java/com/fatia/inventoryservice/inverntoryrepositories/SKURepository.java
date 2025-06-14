package com.fatia.inventoryservice.inverntoryrepositories;

import com.fatia.inventoryservice.inventoryentities.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SKURepository extends JpaRepository<SKUEntity, Long> {
    Optional<SKUEntity> getSKUEntityByCode(String code);

    int countByCode(String code);

    Optional<SKUEntity> findSKUEntityByShelfId(Long shelfId);
}
