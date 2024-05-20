package com.hsbc.ledger.controller;

import com.hsbc.ledger.entity.Posting;
import com.hsbc.ledger.service.PostingQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/postings")
public class PostingQueryController {

    @Autowired
    private PostingQueryService queryService;

    @GetMapping
    public List<Posting> fetchAllPostings(){
        return queryService.getPostings();
    }

}



