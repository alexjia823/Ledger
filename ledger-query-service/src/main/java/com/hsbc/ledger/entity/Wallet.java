package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_query")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    @Id
    private Long id;
    private String name;
    private Double balance;
    private Long accountID;
    @Transient
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}
