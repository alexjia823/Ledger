package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.WalletEvent;
import com.hsbc.ledger.entities.Wallet;
import com.hsbc.ledger.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletCommandService {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public Wallet createWallet(Wallet wallet){
        Wallet walletDO = repository.save(wallet);
        WalletEvent event=new WalletEvent("CreateWallet", walletDO);
        kafkaTemplate.send("Wallet-event-topic", event);
        return walletDO;
    }

    public Wallet updateWallet(long id, Wallet newWallet){
        Wallet existingWallet = repository.findById(id).get();
        existingWallet.setName(newWallet.getName());
        existingWallet.setBalance(newWallet.getBalance());
        existingWallet.setAccountID(newWallet.getAccountID());
        Wallet walletDO = repository.save(existingWallet);
        WalletEvent event=new WalletEvent("UpdateWallet", walletDO);
        kafkaTemplate.send("Wallet-event-topic", event);
        return walletDO;
    }

}