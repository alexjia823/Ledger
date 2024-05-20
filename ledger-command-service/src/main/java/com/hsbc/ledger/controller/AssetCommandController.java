package com.hsbc.ledger.controller;


import com.hsbc.ledger.entities.Asset;
import com.hsbc.ledger.service.AssetCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
public class AssetCommandController {

    @Autowired
    private AssetCommandService commandService;

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return commandService.createAsset(asset);
    }

    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable long id, @RequestBody Asset asset) {
        return commandService.updateAsset(id, asset);
    }


}



