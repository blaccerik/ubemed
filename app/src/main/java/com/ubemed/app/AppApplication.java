package com.ubemed.app;

import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@SpringBootApplication
public class AppApplication {

    @Autowired
    StoreService storeService;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)  // every hour from run
    public void doScheduledWork() {
        storeService.endBids(new Date());
    }
}
