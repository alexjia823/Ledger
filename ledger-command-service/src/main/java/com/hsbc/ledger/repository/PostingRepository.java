package com.hsbc.ledger.repository;


import com.hsbc.ledger.entities.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting,Long> {


}
