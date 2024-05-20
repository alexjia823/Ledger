package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.EntityEvent;
import com.hsbc.ledger.entities.EntityObject;
import com.hsbc.ledger.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EntityCommandService {

    @Autowired
    private EntityRepository repository;

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public EntityObject createEntityObject(EntityObject entity){
        EntityObject entityD0 = repository.save(entity);
        EntityEvent event=new EntityEvent("CreateEntityObject", entityD0);
        kafkaTemplate.send("Entity-event-topic", event);
        return entityD0;
    }

    public EntityObject updateEntityObject(long id, EntityObject newEntityObject){
        EntityObject existingEntityObject = repository.findById(id).get();
        existingEntityObject.setName(newEntityObject.getName());
        existingEntityObject.setDescription(newEntityObject.getDescription());
        EntityObject EntityObjectDO = repository.save(existingEntityObject);
        EntityEvent event=new EntityEvent("UpdateEntityObject", EntityObjectDO);
        kafkaTemplate.send("Entity-event-topic", event);
        return EntityObjectDO;
    }

}