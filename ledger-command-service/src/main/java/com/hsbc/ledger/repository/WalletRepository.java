package com.hsbc.ledger.repository;


import com.hsbc.ledger.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long> {


}
