package com.hsbc.ledger.entity;

import com.hsbc.ledger.utils.AccountState;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_query")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private Long id;
    private String name;
    private AccountState state;
    private String description;
}
