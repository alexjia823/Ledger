package com.hsbc.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletMovement {
    private Long accountID;
    private Long fromWalletID;
    private Long toWalletID;
    private Long fromAssetID;
    private Long toAssetID;
    private Double amount;
}
