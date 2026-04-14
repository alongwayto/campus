package com.campus.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusDeviceApplication.class, args);
    }
}
