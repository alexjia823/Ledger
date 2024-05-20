package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.PostingEvent;
import com.hsbc.ledger.entities.Posting;
import com.hsbc.ledger.repository.PostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostingCommandService {

    @Autowired
    private PostingRepository repository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public Posting createPosting(Posting posting){
        Posting postingDO = repository.save(posting);
        PostingEvent event=new PostingEvent("CreatePosting", postingDO);
        kafkaTemplate.send("Posting-event-topic", event);
        return postingDO;
    }

    public Posting updatePosting(long id, Posting newPosting){
        Posting existingPosting = repository.findById(id).get();
        existingPosting.setAmount(newPosting.getAmount());
        existingPosting.setState(newPosting.getState());
        existingPosting.setTimestamp(newPosting.getTimestamp());
        existingPosting.setFromAssetID(newPosting.getFromAssetID());
        existingPosting.setFromWalletID(newPosting.getFromWalletID());
        existingPosting.setToAssetID(newPosting.getToAssetID());
        existingPosting.setToWalletID(newPosting.getToWalletID());
        existingPosting.setAccountID(newPosting.getAccountID());
        Posting postingDO = repository.save(existingPosting);
        PostingEvent event=new PostingEvent("UpdatePosting", postingDO);
        kafkaTemplate.send("Posting-event-topic", event);
        return postingDO;
    }

}