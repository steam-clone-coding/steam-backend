package com.clonecoding.steam.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GlobalController {

    @GetMapping("/health-check")
    public String healthCheck(){
        return "server is running!";
    }
}
