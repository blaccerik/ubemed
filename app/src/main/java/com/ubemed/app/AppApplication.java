package com.ubemed.app;

import com.ubemed.app.model.WheelWinner;
import com.ubemed.app.service.CasinoService;
import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Random;

@SpringBootApplication
public class AppApplication {

    @Autowired
    StoreService storeService;

    @Autowired
    CasinoService casinoService;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)  // every hour from run
    public void doScheduledWork() {
        storeService.endBids(new Date());
    }

    @Scheduled(fixedRate = 1000 * 60)  // every minute from run
    public void spinWheel() {
        WheelWinner wheelWinner = casinoService.spin(new Date(), new Random().nextDouble());

        // todo broadcast win
    }
}
