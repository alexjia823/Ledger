package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.WalletEvent;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletQueryService {

    @Autowired
    private WalletRepository repository;

    public List<Wallet> getWallets() {
        return repository.findAll();
    }

    public Optional<Wallet> getWallet(Long id) {
        return repository.findById(id);
    }

    @KafkaListener(topics = "Wallet-event-topic",groupId = "Wallet-event-group")
    public void processWalletEvents(WalletEvent WalletEvent) {
        Wallet wallet = WalletEvent.getWallet();
        if (WalletEvent.getEventType().equals("CreateWallet")) {
            repository.save(wallet);
        }
        if (WalletEvent.getEventType().equals("UpdateWallet")) {
            Wallet existingWallet = repository.findById(wallet.getId()).get();
            existingWallet.setName(wallet.getName());
            existingWallet.setAccountID(wallet.getAccountID());
            existingWallet.setBalance(wallet.getBalance());
            repository.save(existingWallet);
        }
    }
}