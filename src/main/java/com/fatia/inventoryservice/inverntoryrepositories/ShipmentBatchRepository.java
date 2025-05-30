package com.fatia.inventoryservice.inverntoryrepositories;

import com.fatia.inventoryservice.inventoryentities.ShipmentBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentBatchRepository extends JpaRepository<ShipmentBatchEntity, Long> {
}
