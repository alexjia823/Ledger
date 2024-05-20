package com.hsbc.ledger.controller;

import com.hsbc.ledger.dto.WalletMovement;
import com.hsbc.ledger.entities.Account;
import com.hsbc.ledger.service.AccountCommandService;
import com.hsbc.ledger.utils.AccountState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountCommandController {

    @Autowired
    private AccountCommandService commandService;

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return commandService.createAccount(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable long id, @RequestBody Account account) {
        return commandService.updateAccount(id, account);
    }

    @PatchMapping("/{id}")
    public Account updateState(@PathVariable long id, @RequestBody AccountState state) {
        return commandService.updateState(id, state);
    }

    //Transfer the asset
    @PostMapping("/transfer")
    public ResponseEntity<String> accountTransfer(@RequestBody WalletMovement walletMovement) {
            return commandService.accountTransfer(walletMovement);
    }

    //Batch transfer the asset
    @PostMapping("/batch")
    public ResponseEntity<String> batchTransfer(@RequestBody List<WalletMovement> walletMovements) {
        try {
            return commandService.batchTransfer(walletMovements);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}