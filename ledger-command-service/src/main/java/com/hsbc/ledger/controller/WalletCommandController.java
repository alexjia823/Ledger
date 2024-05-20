package com.hsbc.ledger.controller;

import com.hsbc.ledger.entities.Wallet;
import com.hsbc.ledger.service.WalletCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletCommandController {

    @Autowired
    private WalletCommandService commandService;

    @PostMapping("/wallet")
    public Wallet createWallet(@RequestBody Wallet wallet) {
        return commandService.createWallet(wallet);
    }

    @PutMapping("/{id}")
    public Wallet updateWallet(@PathVariable long id, @RequestBody Wallet wallet) {
        return commandService.updateWallet(id, wallet);
    }
}