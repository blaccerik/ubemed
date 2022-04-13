package com.ubemed.app.service;


import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class StoreServiceTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void makeBid() {

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        userRepository.save(dbUser);

        System.out.println(userRepository);

        DBUser opt = userRepository.findByName("1").get();

        assertEquals(opt.getCoins(), 100);

//        StoreService storeService = new StoreService(productRepository, userRepository, null);
//        storeService.makeBid("1", 1, 10);


    }

    @Test
    void endBids() {
    }
}