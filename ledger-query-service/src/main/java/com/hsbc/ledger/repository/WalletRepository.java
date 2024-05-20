package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
}
