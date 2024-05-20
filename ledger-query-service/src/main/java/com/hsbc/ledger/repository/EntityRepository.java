package com.hsbc.ledger.repository;


import com.hsbc.ledger.entity.Account;
import com.hsbc.ledger.entity.EntityObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<EntityObject,Long> {
}
