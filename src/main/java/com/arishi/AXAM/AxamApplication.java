package com.arishi.AXAM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AxamApplication {

    public static void main(String[] args) {
        SpringApplication.run(AxamApplication.class, args);
    }

}
