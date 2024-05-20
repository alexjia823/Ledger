package com.hsbc.ledger.dto;

import com.hsbc.ledger.entities.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletEvent {
    private String eventType;
    private Wallet wallet;
}
