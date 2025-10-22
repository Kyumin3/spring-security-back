package com.example.lkm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class LkmApplication {

    private static final Logger logger = LoggerFactory.getLogger(LkmApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LkmApplication.class, args);
    }

}
