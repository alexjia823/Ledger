package com.hsbc.ledger.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "asset_command")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private Double balance;
    private Long walletID;
}
