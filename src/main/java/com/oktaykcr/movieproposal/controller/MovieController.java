package com.oktaykcr.movieproposal.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @GetMapping("/")
    @ApiOperation(value = "This is test value", notes = "these are notes")
    public String helloworld() {
        return "Hello World";
    }

}
