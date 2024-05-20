package com.hsbc.ledger.entities;

import com.hsbc.ledger.utils.PostingState;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "posting_command")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Posting {
    @Id
    @GeneratedValue
    private Long id;
    private Long fromWalletID;
    private Long toWalletID;
    private Long fromAssetID;
    private Long toAssetID;
    private LocalDateTime timestamp;
    private PostingState state;
    private Double amount;
    private Long accountID;
}