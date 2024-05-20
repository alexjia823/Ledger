package com.hsbc.ledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asset_query")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    @Id
    private Long id;
    private String type;
    private Double balance;
    private Long walletID;
    @Transient
    @OneToOne(cascade = CascadeType.ALL)
    private Wallet wallet;
}