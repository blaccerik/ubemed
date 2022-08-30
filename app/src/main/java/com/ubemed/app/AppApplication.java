package com.ubemed.app;

import com.ubemed.app.dtomodel.DTOUser;
import com.ubemed.app.dtomodel.WheelEnterBroadcast;
import com.ubemed.app.dtomodel.WheelWinner;
import com.ubemed.app.service.CasinoService;
import com.ubemed.app.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
public class AppApplication {

    @Autowired
    private StoreService storeService;

    @Autowired
    private CasinoService casinoService;

    @Autowired
    private SimpMessagingTemplate template;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60)  // every hour from run
    public void doScheduledWork() {
        storeService.endBids(new Date());
    }

    @Scheduled(fixedRate = 1000 * 10)  // every minute from run
    public void spinWheel() {
        Date date = new Date();
        date.setTime(date.getTime());
        WheelWinner wheelWinner = casinoService.spin(date, new Random().nextDouble());
        casinoService.reset();

        WheelEnterBroadcast wheelEnterBroadcast;
        if (wheelWinner.getDbUser() != null) {

            DTOUser dtoUser = new DTOUser(wheelWinner.getDbUser().getName());

            wheelEnterBroadcast = new WheelEnterBroadcast(
                    false,
                0,
                dtoUser,
                wheelWinner.getValue(),
                wheelWinner.getPlayers()
            );
        } else {
            wheelEnterBroadcast = new WheelEnterBroadcast(false,0, null, 0, new ArrayList<>());
        }
        System.out.println(wheelEnterBroadcast.getUser() + " won " + wheelEnterBroadcast.getValue());
        template.convertAndSend("/casino", wheelEnterBroadcast);
    }
}
