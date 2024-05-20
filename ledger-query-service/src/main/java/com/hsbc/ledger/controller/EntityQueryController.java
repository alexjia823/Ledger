package com.hsbc.ledger.controller;

import com.hsbc.ledger.entity.EntityObject;
import com.hsbc.ledger.service.EntityQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entities")
public class EntityQueryController {

    @Autowired
    private EntityQueryService queryService;

    @GetMapping
    public List<EntityObject> fetchAllEntities(){
        return queryService.getEntities();
    }

    @GetMapping("/{id}")
    public Optional<EntityObject> getEntityByID(@PathVariable long id) {
        Optional<EntityObject> entity = queryService.getEntity(id);
        return entity;
    }

}



