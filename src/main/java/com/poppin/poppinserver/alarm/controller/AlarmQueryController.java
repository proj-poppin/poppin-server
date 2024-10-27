package com.poppin.poppinserver.alarm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.write.enabled", havingValue = "false")
@RequestMapping("/api/v1/alarm")
public class AlarmQueryController {

}
