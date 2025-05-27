package com.fatia.inventoryservice.inverntoryrepositories;

import com.fatia.inventoryservice.inventoryentities.SKU;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SKURepository extends JpaRepository<SKU, Long> {
}
