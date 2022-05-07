package com.ubemed.app.config;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.BidResponse;
import com.ubemed.app.model.UserResponse;
import com.ubemed.app.model.WheelEnterBroadcast;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.service.CasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    static int n = 0;

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CasinoService casinoService;

//    @Scheduled(fixedDelay = 3000)
//    public void sendAdhocMessages() {
//        template.convertAndSend("/topic/user", new UserResponse("Fixed Delay Scheduler"));
////        System.out.println("end");
//    }

//    @Scheduled(fixedDelay = 1500)
//    private void updateBids() {
//        n += 1;
//        if (userRepository.findAll().size() > 0 && productRepository.findAll().size() > 0) {
//            DBUser dbUser = userRepository.findAll().get(n % userRepository.findAll().size());
//
//            DBProduct dbProduct = productRepository.findAll().get(n % productRepository.findAll().size());
//            System.out.println(dbUser.getName() + " " + dbProduct.getId());
//            template.convertAndSend("/bids", new BidResponse(dbUser.getName(), dbProduct.getId(), 15 + n));
//        }
//    }
//
//    @Scheduled(fixedDelay = 1500)
//    private void updateBids() {
//        casinoService.update("name " + n % 26, n % 15, n);
//        n += 1;
////        template.convertAndSend("/casino", wheelEnterBroadcast);
//    }

    @Scheduled(fixedDelay = 100)
    private void casinoEnter() {
        List<String> list = List.of("string", "string2","string3", "string4","string5", "string6");
        String name = list.get(n % list.size());
        long val = casinoService.enter(name, List.of(), n % 25, new Date());
//        System.out.println(list.get(n % 2) + " " + val);

        if (val > 0) {
            casinoService.update(name, n % 25, n % 25);
        }

        n += 1;
    }
}
