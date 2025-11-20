package com.user.account.app.controller;

import com.user.account.app.service.AuthorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class NPlusOneDemoController {

    private final AuthorService authorService;

    public NPlusOneDemoController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/n-plus-one-problem")
    public String demonstrateNPlusOneProblem() {
        authorService.demonstrateNPlusOneProblem();
        return "Check the console logs to see the N+1 query problem! You'll see multiple SELECT queries.";
    }

    @GetMapping("/fetch-join-solution")
    public String demonstrateFetchJoinSolution() {
        authorService.demonstrateFetchJoinSolution();
        return "Check the console logs to see the FETCH JOIN solution! You'll see only 1 query.";
    }

    @GetMapping("/entity-graph-solution")
    public String demonstrateEntityGraphSolution() {
        authorService.demonstrateEntityGraphSolution();
        return "Check the console logs to see the EntityGraph solution! You'll see only 1 query.";
    }
}
