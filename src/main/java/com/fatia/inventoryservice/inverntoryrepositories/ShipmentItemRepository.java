package com.fatia.inventoryservice.inverntoryrepositories;

import com.fatia.inventoryservice.inventoryentities.ShipmentItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShipmentItemRepository extends JpaRepository<ShipmentItemEntity, Long> {
    @Query("SELECT COUNT(si) > 0 FROM ShipmentItemEntity si WHERE si.sku.id = :skuId")
    boolean isSkuUsedInShipment(@Param("skuId") Long skuId);
}
