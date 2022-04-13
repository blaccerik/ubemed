package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StoreServiceTest {

    @Resource
    UserRepository userRepository;

    @Test
    void makeBid() {

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        userRepository.save(dbUser);

        DBUser opt = userRepository.findByName("1").get();

        assertEquals(opt.getCoins(), 100);

//        StoreService storeService = new StoreService(productRepository, userRepository, null);
//        storeService.makeBid("1", 1, 10);


    }

    @Test
    void endBids() {
    }
}