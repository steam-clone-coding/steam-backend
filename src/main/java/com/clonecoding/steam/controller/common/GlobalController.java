package com.clonecoding.steam.controller.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class GlobalController {

    @GetMapping("/health-check")
    public String healthCheck(){
        return "server is running!";
    }
}
