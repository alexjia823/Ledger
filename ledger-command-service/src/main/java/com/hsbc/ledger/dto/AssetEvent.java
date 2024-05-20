package com.hsbc.ledger.dto;


import com.hsbc.ledger.entities.Asset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetEvent {

    private String eventType;
    private Asset asset;
}
