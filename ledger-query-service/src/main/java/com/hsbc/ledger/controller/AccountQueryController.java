package com.hsbc.ledger.controller;

import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.service.AccountQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountQueryController {

    @Autowired
    private AccountQueryService queryService;

    @GetMapping
    public List<Account> fetchAllAccounts(){
        return queryService.getAccounts();
    }

    @GetMapping("/{id}")
    public Optional<Account> getAccountByID(@PathVariable long id) {
        Optional<Account> account = queryService.getAccount(id);
        return account;
    }

}



