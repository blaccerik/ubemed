package com.ubemed.app.service;


import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.BidRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class StoreServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BidRepository bidRepository;

    @Test
    void makeBid() {

        userRepository.deleteAll();
        productRepository.deleteAll();


        boolean value;
        DBUser dbUser1 = new DBUser();
        dbUser1.setId(1);
        dbUser1.setName("1");
        dbUser1.setCoins(100);
        userRepository.save(dbUser1);

        DBUser dbUser2 = new DBUser();
        dbUser2.setId(2);
        dbUser2.setName("2");
        dbUser2.setCoins(100);
        userRepository.save(dbUser2);

        DBUser dbUser3 = new DBUser();

        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser3);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(10);
        dbProduct.setBids(new ArrayList<>());

        dbUser3.setId(3);
        dbUser3.setName("3");
        dbUser3.setCoins(100);
        dbUser3.setProducts(List.of(dbProduct));


        userRepository.save(dbUser3);

        long id = productRepository.findAll().get(0).getId();

        StoreService storeService = new StoreService(productRepository, userRepository, bidRepository,null);

        value = storeService.makeBid("3", id, 0);
        Assertions.assertEquals(value, false);
        value = storeService.makeBid("3", id, 11);
        Assertions.assertEquals(value, false);

        value = storeService.makeBid("2", id, 11);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 89);

        value = storeService.makeBid("2", id, 11);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 89);

        value = storeService.makeBid("2", id, 12);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 88);

        value = storeService.makeBid("3", id, 100);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 100);

        value = storeService.makeBid("1", id, 10);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 100);

        value = storeService.makeBid("1", id, 15);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 85);

        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 15);

    }

    @Test
    void endBids() {

        userRepository.deleteAll();
        productRepository.deleteAll();


        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        userRepository.saveAndFlush(dbUser);

        DBUser dbUser2 = new DBUser();
        dbUser2.setCoins(100);
        dbUser2.setName("2");
        userRepository.saveAndFlush(dbUser2);

        DBUser dbUser3 = new DBUser();
        dbUser3.setCoins(100);
        dbUser3.setName("3");
        userRepository.saveAndFlush(dbUser3);

        DBUser dbUser4 = new DBUser();
        dbUser4.setCoins(100);
        dbUser4.setName("4");
        userRepository.save(dbUser4);

        DBUser dbUser5 = userRepository.findByName("4").get();

        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser5);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(10);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));

        dbUser5.getProducts().add(dbProduct);
        userRepository.save(dbUser5);

        StoreService storeService = new StoreService(productRepository, userRepository, bidRepository,null);

        long id = productRepository.findAll().get(0).getId();

        Assertions.assertEquals(storeService.makeBid("4", id, 11), false);
        Assertions.assertEquals(storeService.makeBid("1", id, 11), true);
        Assertions.assertEquals(storeService.makeBid("2", id, 12), true);
        Assertions.assertEquals(storeService.makeBid("2", id, 13), true);
        Assertions.assertEquals(storeService.makeBid("1", id, 14), true);
        Assertions.assertEquals(storeService.makeBid("3", id, 15), true);
        Assertions.assertEquals(storeService.makeBid("1", id, 16), true);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 84);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 87);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 85);
        Assertions.assertEquals(userRepository.findByName("4").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 0);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);

        storeService.endBids(UserServiceTest.parseDate("2022-05-06"));

        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);
        Assertions.assertEquals(productRepository.findById(id).get().isOnSale(), false);

        Assertions.assertEquals(userRepository.findByName("4").get().getProducts().size(), 0);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 84);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 100);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 100);
        Assertions.assertEquals(userRepository.findByName("4").get().getCoins(), 116);
        Assertions.assertEquals(productRepository.findById(id).get().getBids().size(), 0);
//
//        dbProduct.setBids(new ArrayList<>());
//
//        dbProduct.setOnSale(true);
//        dbProduct.setPrice(15);
//        dbProduct.setHighestBid(15);
//        storeService.endBids(UserServiceTest.parseDate("2022-05-06"));
//        Assertions.assertEquals(dbProduct.getDbUser().getId(), 1);
//        Assertions.assertEquals(dbUser1.getProducts().size(), 1);
//        Assertions.assertEquals(dbProduct.getPrice(), 15);
//        Assertions.assertEquals(dbUser1.getCoins(), 99);
//        Assertions.assertEquals(dbProduct.isOnSale(), false);
    }
}