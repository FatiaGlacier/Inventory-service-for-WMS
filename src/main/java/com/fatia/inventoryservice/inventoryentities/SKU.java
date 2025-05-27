package com.fatia.inventoryservice.inventoryentities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "storage_keeping_units")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SKU { // Storage keeping unit
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private SKUStatus status;

    @Column(nullable = false)
    private int length;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    private Integer quantity;

    @Column(nullable = false)
    private Long shelfId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Type(io.hypersistence.utils.hibernate.type.json.JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            columnDefinition = "jsonb")
    private Map<String, String> storageConditions;
}
