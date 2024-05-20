package com.hsbc.ledger.repository;

import com.hsbc.ledger.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting,Long> {
}
