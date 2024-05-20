package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.PostingEvent;
import com.hsbc.ledger.entity.Posting;
import com.hsbc.ledger.repository.PostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostingQueryService {

    @Autowired
    private PostingRepository repository;

    public List<Posting> getPostings() {
        return repository.findAll();
    }

    public Optional<Posting> getPosting(Long id) {
        return repository.findById(id);
    }

    @KafkaListener(topics = "Posting-event-topic",groupId = "Posting-event-group")
    public void processPostingEvents(PostingEvent postingEvent) {
        Posting posting = postingEvent.getPosting();
        if (postingEvent.getEventType().equals("CreatePosting")) {
            repository.save(posting);
        }
        if (postingEvent.getEventType().equals("UpdatePosting")) {
            Posting existingPosting = repository.findById(posting.getId()).get();
            existingPosting.setAmount(posting.getAmount());
            existingPosting.setState(posting.getState());
            existingPosting.setTimestamp(posting.getTimestamp());
            existingPosting.setFromAssetID(posting.getFromAssetID());
            existingPosting.setFromWalletID(posting.getFromWalletID());
            existingPosting.setToAssetID(posting.getToAssetID());
            existingPosting.setToWalletID(posting.getToWalletID());
            existingPosting.setAccountID(posting.getAccountID());
            repository.save(existingPosting);
        }
    }
}