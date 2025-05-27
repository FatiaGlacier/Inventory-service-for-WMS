package com.fatia.inventoryservice.warehouserepositories;

import com.fatia.inventoryservice.warehouseentities.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
}
