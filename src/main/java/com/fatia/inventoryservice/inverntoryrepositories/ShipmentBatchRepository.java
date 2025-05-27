package com.fatia.inventoryservice.inverntoryrepositories;

import com.fatia.inventoryservice.inventoryentities.ShipmentBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentBatchRepository extends JpaRepository<ShipmentBatch, Long> {
}
