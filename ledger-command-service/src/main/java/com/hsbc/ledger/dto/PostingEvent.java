package com.hsbc.ledger.dto;


import com.hsbc.ledger.entities.Posting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostingEvent {

    private String eventType;
    private Posting posting;
}
