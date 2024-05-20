package com.hsbc.ledger.repository;


import com.hsbc.ledger.entities.Account;
import com.hsbc.ledger.entities.EntityObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<EntityObject,Long> {


}
