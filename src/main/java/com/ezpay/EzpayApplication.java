package com.ezpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EzpayApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(EzpayApplication.class);

    public static void main(String[] args) {

        logger.info("Starting EZPAY Application...");

        SpringApplication.run(EzpayApplication.class, args);

        logger.info("EZPAY Application started successfully.");
    }
}