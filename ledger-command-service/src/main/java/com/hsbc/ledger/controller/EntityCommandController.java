package com.hsbc.ledger.controller;

import com.hsbc.ledger.entities.EntityObject;
import com.hsbc.ledger.service.EntityCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entities")
public class EntityCommandController {

    @Autowired
    private EntityCommandService commandService;

    @PostMapping
    public EntityObject createEntity(@RequestBody EntityObject entity) {
        return commandService.createEntityObject(entity);
    }

    @PutMapping("/{id}")
    public EntityObject updateEntity(@PathVariable long id, @RequestBody EntityObject entity) {
        return commandService.updateEntityObject(id, entity);
    }


}



