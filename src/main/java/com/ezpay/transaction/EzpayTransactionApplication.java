package com.ezpay.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EzpayTransactionApplication {

    private static final Logger logger =
            LoggerFactory.getLogger(EzpayTransactionApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EzpayTransactionApplication.class, args);
        logger.info("EZPay Transaction application started");
    }
}