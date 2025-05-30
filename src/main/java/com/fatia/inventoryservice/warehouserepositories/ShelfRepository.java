package com.fatia.inventoryservice.warehouserepositories;

import com.fatia.inventoryservice.warehouseentities.ShelfEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<ShelfEntity, Long> {
}
