package com.fatia.inventoryservice.warehouseentities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Map;

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

    private String name;

    @Type(io.hypersistence.utils.hibernate.type.json.JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            columnDefinition = "jsonb")
    private Map<String, String> conditions;

    private boolean isOccupied;
}
