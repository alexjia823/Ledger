package com.hsbc.ledger.controller;

import com.hsbc.ledger.entity.Asset;
import com.hsbc.ledger.entity.Wallet;
import com.hsbc.ledger.service.AssetQueryService;
import com.hsbc.ledger.service.WalletQueryService;
import com.hsbc.ledger.service.AccountQueryService;
import com.hsbc.ledger.service.PostingQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assets")
public class AssetQueryController {

    @Autowired
    private AssetQueryService assetQueryService;

    @Autowired
    private WalletQueryService walletQueryService;

    @Autowired
    private PostingQueryService postingQueryService;

    @Autowired
    private AccountQueryService accoutQueryService;

    @GetMapping
    public List<Asset> fetchAllAssets(){
        return assetQueryService.getAssets();
    }

    @GetMapping("/{id}")
    public Optional<Asset> getAssetByID(@PathVariable long id) {
        Optional<Asset> asset = assetQueryService.getAsset(id);
        if(asset.isPresent()) {
            Long walletID = asset.get().getWalletID();
            Optional<Wallet> wallet = walletQueryService.getWallet(walletID);
            asset.get().setWallet(wallet.get());
        }
        return asset;
    }

}



