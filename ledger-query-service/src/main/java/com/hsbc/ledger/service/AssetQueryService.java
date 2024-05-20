package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.AssetEvent;
import com.hsbc.ledger.entity.Asset;
import com.hsbc.ledger.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetQueryService {

    @Autowired
    private AssetRepository repository;

    public List<Asset> getAssets() {
        return repository.findAll();
    }

    public Optional<Asset> getAsset(Long id) {
        return repository.findById(id);
    }

    @KafkaListener(topics = "Asset-event-topic",groupId = "Asset-event-group")
    public void processAssetEvents(AssetEvent AssetEvent) {
        Asset asset = AssetEvent.getAsset();
        if (AssetEvent.getEventType().equals("CreateAsset")) {
            repository.save(asset);
        }
        if (AssetEvent.getEventType().equals("UpdateAsset")) {
            Asset existingAsset = repository.findById(asset.getId()).get();
            existingAsset.setType(asset.getType());
            existingAsset.setWalletID(asset.getWalletID());
            existingAsset.setBalance(asset.getBalance());
            repository.save(existingAsset);
        }
    }
}