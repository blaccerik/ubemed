package com.ubemed.app.config;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.UserResponse;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    static int n = 0;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    UserRepository userRepository;

//    @Scheduled(fixedDelay = 3000)
//    public void sendAdhocMessages() {
//        template.convertAndSend("/topic/user", new UserResponse("Fixed Delay Scheduler"));
////        System.out.println("end");
//    }

//    @Scheduled(fixedDelay = 500)
//    private void updateBids() {
//        n += 1;
//        DBUser dbUser = userRepository.findAll().get(n % userRepository.findAll().size());
//        template.convertAndSend("/bids/" + 9, new BidResponse(dbUser.getName(), 15 + n));
//    }
}
