package com.example.daoumarket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/home")
@Tag(name = "Home", description = "Home API Document")
public class HomeController {

    @GetMapping("/hello")
    @Operation(summary = "Hello", description = "Hello, Stranger")
    public String test() {
        return "Hello, stranger.";
    }
}
