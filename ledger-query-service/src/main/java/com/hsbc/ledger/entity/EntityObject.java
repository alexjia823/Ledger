package com.hsbc.ledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entity_query")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityObject {
    @Id
    private long id;
    private String name;
    private String description;
}
