package com.hsbc.ledger.repository;

import com.hsbc.ledger.entities.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset,Long> {


}
