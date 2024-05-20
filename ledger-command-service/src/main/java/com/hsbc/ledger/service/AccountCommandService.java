package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.*;
import com.hsbc.ledger.entities.*;
import com.hsbc.ledger.repository.AccountRepository;
import com.hsbc.ledger.repository.AssetRepository;
import com.hsbc.ledger.repository.PostingRepository;
import com.hsbc.ledger.repository.WalletRepository;
import com.hsbc.ledger.utils.AccountState;
import com.hsbc.ledger.utils.PostingState;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountCommandService {

    private final String walletQueryUrl = "http://localhost:9191/wallets/";
    private final String assetQueryUrl = "http://localhost:9191/assets/";
    private final String accountQueryUrl = "http://localhost:9191/accounts/";

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AssetRepository assetRepo;

    @Autowired
    private PostingRepository postingRepo;

    @Autowired
    private WalletRepository walletRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public Account createAccount(Account account) {
        Account AccountDO = accountRepo.save(account);
        AccountEvent event = new AccountEvent("CreateAccount", AccountDO);
        kafkaTemplate.send("Account-event-topic", event);
        return AccountDO;
    }

    public Account updateAccount(long id, Account newAccount) {
        Account existingAccount = accountRepo.findById(id).get();
        existingAccount.setName(newAccount.getName());
        existingAccount.setState(newAccount.getState());
        existingAccount.setDescription(newAccount.getDescription());
        Account accountDO = accountRepo.save(existingAccount);
        AccountEvent event = new AccountEvent("UpdateAccount", accountDO);
        kafkaTemplate.send("Account-event-topic", event);
        return accountDO;
    }

    public Account updateState(long id, AccountState state) {
        Account existingAccount = accountRepo.findById(id).get();
        existingAccount.setState(state);
        Account accountDO = accountRepo.save(existingAccount);
        AccountEvent event = new AccountEvent("UpdateAccount", accountDO);
        kafkaTemplate.send("Account-event-topic", event);
        return accountDO;
    }

    private Asset getAssetFromRemote(Long assetID){
        RestTemplate restTemplate = new RestTemplate();
        String url = assetQueryUrl + assetID;
        Asset asset = restTemplate.getForObject(url, Asset.class);
        if (asset == null) {
            throw new NullPointerException("The asset is null");
        }
        return asset;
    }

    private Wallet getWalletFromRemote(Long walletID){
        RestTemplate restTemplate = new RestTemplate();
        String url = walletQueryUrl + walletID;
        Wallet wallet = restTemplate.getForObject(url, Wallet.class);
        if (wallet == null) {
            throw new NullPointerException("The wallet is null");
        }
        return wallet;
    }

    private Account getAccountFromRemote(Long acctID){
        RestTemplate restTemplate = new RestTemplate();
        String url = accountQueryUrl + acctID;
        Account account = restTemplate.getForObject(url, Account.class);
        if (account == null) {
            throw new NullPointerException("The account is null");
        }
        return account;
    }

    private void singleTransfer(WalletMovement walletMovement){
        Posting newPosting = null;
        try {
            // Add new posting and set its state to pending
            // and then send to kafka to sync with ledger_query_service
            newPosting = new Posting();
            newPosting.setFromWalletID(walletMovement.getFromWalletID());
            newPosting.setToWalletID(walletMovement.getToWalletID());
            newPosting.setFromAssetID(walletMovement.getFromAssetID());
            newPosting.setToAssetID(walletMovement.getToAssetID());
            newPosting.setTimestamp(LocalDateTime.now());
            newPosting.setState(PostingState.PENDING);
            newPosting.setAmount(walletMovement.getAmount());
            newPosting.setAccountID(walletMovement.getAccountID());
            Posting newPostingRtn = postingRepo.save(newPosting);
            PostingEvent eventPostingRtn = new PostingEvent("CreatePosting", newPostingRtn);
            kafkaTemplate.send("Posting-event-topic", eventPostingRtn);

            Long fromAssetID = walletMovement.getFromAssetID();
            Asset fromAsset = getAssetFromRemote(fromAssetID);

            Long fromWalletID = walletMovement.getFromWalletID();
            Wallet fromWallet = getWalletFromRemote(fromWalletID);

            Long toWalletID = walletMovement.getToWalletID();
            Wallet toWallet = getWalletFromRemote(toWalletID);

            // if asset balance is not sufficient
            if (fromAsset.getBalance() < walletMovement.getAmount()) {
                throw new IllegalArgumentException("From asset balance is insufficient");
            }

            // can't transfer between accounts
            if (fromWallet.getAccountID() != toWallet.getAccountID()) {
                throw new IllegalArgumentException("is not able to transfer between different account");
            }

            Long toAssetID = walletMovement.getToAssetID();
            Asset toAsset = getAssetFromRemote(toAssetID);

            fromWallet.setBalance(fromWallet.getBalance() - walletMovement.getAmount());
            Wallet fromWalletRtn = walletRepo.save(fromWallet);
            WalletEvent eventFromWalletRtn = new WalletEvent("UpdateWallet", fromWalletRtn);

            toWallet.setBalance(toWallet.getBalance() + walletMovement.getAmount());
            Wallet toWalletRtn = walletRepo.save(toWallet);
            WalletEvent eventToWalletRtn = new WalletEvent("UpdateWallet", toWalletRtn);

            fromAsset.setBalance(fromAsset.getBalance() - walletMovement.getAmount());
            Asset fromAssetRtn = assetRepo.save(fromAsset);
            AssetEvent eventFromAssetRtn = new AssetEvent("UpdateAsset", fromAssetRtn);

            toAsset.setBalance(toAsset.getBalance() + walletMovement.getAmount());
            Asset toAssetRtn = assetRepo.save(toAsset);
            AssetEvent eventToAssetRtn = new AssetEvent("UpdateAsset", toAssetRtn);

            // send the modified wallet and modified asset to kafka to sync
            kafkaTemplate.send("Wallet-event-topic", eventToWalletRtn);
            kafkaTemplate.send("Wallet-event-topic", eventFromWalletRtn);
            kafkaTemplate.send("Asset-event-topic", eventToAssetRtn);
            kafkaTemplate.send("Asset-event-topic", eventFromAssetRtn);

            // setting the posting state to CLEARED after finished the transaction
            newPosting.setState(PostingState.CLEARED);
            Posting updatePostingRtn = postingRepo.save(newPosting);
            PostingEvent updateEventPosting = new PostingEvent("UpdatePosting", updatePostingRtn);
            kafkaTemplate.send("Posting-event-topic", updateEventPosting);

        } catch (Exception e) {
            // If there is exception set the posting state to FAILED
            newPosting.setState(PostingState.FAILED);
            Posting updatePostingRtn = postingRepo.save(newPosting);
            PostingEvent updateEventPosting = new PostingEvent("UpdatePosting", updatePostingRtn);
            kafkaTemplate.send("Posting-event-topic", updateEventPosting);
            throw new RuntimeException("Error occurred during accountTransfer", e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> batchTransfer(List<WalletMovement> walletMovements) throws InterruptedException {

        for (WalletMovement walletMovement : walletMovements) {
            Thread.sleep(800);
            if (walletMovement == null) {
                throw new NullPointerException("The walletMovement is null");
            }
            Account acct = getAccountFromRemote(walletMovement.getAccountID());
            AccountState as = acct.getState();
            if (as != AccountState.OPEN) {
                throw new IllegalArgumentException("the account " + acct.getId() + " is not open");
            }
            singleTransfer(walletMovement);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Transaction successfully");
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> accountTransfer(WalletMovement walletMovement) {
        Account acct = getAccountFromRemote(walletMovement.getAccountID());
        AccountState as = acct.getState();
        if (as == AccountState.OPEN) {
            singleTransfer(walletMovement);
            return ResponseEntity.status(HttpStatus.OK).body("Transaction successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The account state is closed");
        }
    }

}