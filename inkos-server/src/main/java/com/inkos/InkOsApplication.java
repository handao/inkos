package com.inkos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InkOsApplication {
    public static void main(String[] args) {
        SpringApplication.run(InkOsApplication.class, args);
    }
}
