package org.event.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventManagementServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventManagementServiceApp.class, args);
    }
}