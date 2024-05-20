package com.hsbc.ledger.controller;

import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.service.WalletQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wallets")
public class WalletQueryController {

    @Autowired
    private WalletQueryService queryService;

    @GetMapping
    public List<Wallet> fetchAllWallets(){

        return queryService.getWallets();
    }

    @GetMapping("/{id}")
    public Optional<Wallet> getWalletByID(@PathVariable long id) {
        Optional<Wallet> wallet = queryService.getWallet(id);
        if(wallet.isPresent()) {
            Long accID = wallet.get().getAccountID();
            System.out.println("accID is " + accID);
            RestTemplate restTemplate = new RestTemplate();
            // 得到wallet所在的account信息并传给wallet
            String url = "http://localhost:9191/accounts/" + accID;
            System.out.println("url: " + url);
            Account account = restTemplate.getForObject(url, Account.class);
            System.out.println("Response: " + account);
            wallet.get().setAccount(account);
        }
        return wallet;
    }

}



