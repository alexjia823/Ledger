package com.hsbc.ledger.dto;


import com.hsbc.ledger.entities.Account;
import com.hsbc.ledger.entities.EntityObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEvent {
    private String eventType;
    private EntityObject entity;
}
