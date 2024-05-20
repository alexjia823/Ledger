package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.AssetEvent;
import com.hsbc.ledger.entities.Asset;
import com.hsbc.ledger.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AssetCommandService {

    @Autowired
    private AssetRepository repository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public Asset createAsset(Asset asset){
        Asset assetD0 = repository.save(asset);
        AssetEvent event=new AssetEvent("CreateAsset", assetD0);
        kafkaTemplate.send("Asset-event-topic", event);
        return assetD0;
    }

    public Asset updateAsset(long id, Asset newAsset){
        Asset existingAsset = repository.findById(id).get();
        existingAsset.setType(newAsset.getType());
        existingAsset.setBalance(newAsset.getBalance());
        Asset AssetDO = repository.save(existingAsset);
        AssetEvent event=new AssetEvent("UpdateAsset", AssetDO);
        kafkaTemplate.send("Asset-event-topic", event);
        return AssetDO;
    }

}