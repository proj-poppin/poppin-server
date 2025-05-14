package com.poppin.poppinserver.health.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class HealthController {
    @GetMapping("/health")
    public String hello() {
        return "hello world!";
    }
}
