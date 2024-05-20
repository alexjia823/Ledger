package com.hsbc.ledger.service;

import com.hsbc.ledger.dto.EntityEvent;
import com.hsbc.ledger.entity.EntityObject;
import com.hsbc.ledger.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntityQueryService {

    @Autowired
    private EntityRepository entityRepo;

    public List<EntityObject> getEntities() {
        return entityRepo.findAll();
    }

    public Optional<EntityObject> getEntity(Long id) {
        return entityRepo.findById(id);
    }

    @KafkaListener(topics = "Entity-event-topic",groupId = "Entity-event-group")
    public void processEntityEvents(EntityEvent EntityEvent) {
        EntityObject entity = EntityEvent.getEntity();
        if (EntityEvent.getEventType().equals("CreateEntityObject")) {
            entityRepo.save(entity);
        }
        if (EntityEvent.getEventType().equals("UpdateEntityObject")) {
            EntityObject existingEntity = entityRepo.findById(entity.getId()).get();
            existingEntity.setName(entity.getName());
            existingEntity.setDescription(entity.getDescription());
            entityRepo.save(existingEntity);
        }
    }
}