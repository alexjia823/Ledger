package com.hsbc.ledger.entities;

import com.hsbc.ledger.utils.AccountState;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entity_command")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityObject {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String description;
}
