package com.fatia.inventoryservice.warehouseentities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "shelves")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ShelfEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String number;

    private Long nodeZoneId;

    private int xOffset;

    private int yOffset;

    private int width;

    private int height;

    private int length;

    private boolean isOccupied;
}
