package com.hsbc.ledger.repository;


import com.hsbc.ledger.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
