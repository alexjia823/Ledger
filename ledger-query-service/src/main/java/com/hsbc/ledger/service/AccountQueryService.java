package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.AccountEvent;
import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountQueryService {

    @Autowired
    private AccountRepository accountRepo;

    public List<Account> getAccounts() {
        return accountRepo.findAll();
    }

    public Optional<Account> getAccount(Long id) {
        return accountRepo.findById(id);
    }

    @KafkaListener(topics = "Account-event-topic",groupId = "Account-event-group")
    public void processAccountEvents(AccountEvent AccountEvent) {
        Account account = AccountEvent.getAccount();
        if (AccountEvent.getEventType().equals("CreateAccount")) {
            accountRepo.save(account);
        }
        if (AccountEvent.getEventType().equals("UpdateAccount")) {
            Account existingAccount = accountRepo.findById(account.getId()).get();
            existingAccount.setName(account.getName());
            existingAccount.setState(account.getState());
            existingAccount.setDescription(account.getDescription());
            accountRepo.save(existingAccount);
        }
    }
}