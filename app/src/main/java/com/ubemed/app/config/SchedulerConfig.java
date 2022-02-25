package com.ubemed.app.config;

import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    @Autowired
    SimpMessagingTemplate template;

//    @Scheduled(fixedDelay = 3000)
//    public void sendAdhocMessages() {
//        template.convertAndSend("/topic/user", new UserResponse("Fixed Delay Scheduler"));
////        System.out.println("end");
//    }

    @Scheduled(fixedDelay = 3000)
    private void updateBids() {
        template.convertAndSend("/bids/" + 9, new BidResponse("username", 15));
    }
}
