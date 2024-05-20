package com.hsbc.ledger.controller;

import com.hsbc.ledger.entities.Posting;
import com.hsbc.ledger.service.PostingCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/postings")
public class PostingCommandController {

    @Autowired
    private PostingCommandService commandService;

    @PostMapping
    public Posting createPosting(@RequestBody Posting posting) {
        return commandService.createPosting(posting);
    }

    @PutMapping("/{id}")
    public Posting updatePosting(@PathVariable long id, @RequestBody Posting posting) {
        return commandService.updatePosting(id, posting);
    }
}



