package com.hsbc.ledger.dto;


import com.hsbc.ledger.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEvent {
    private String eventType;
    private Account account;
}
