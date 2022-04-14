package com.ubemed.app.service;


import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
public class StoreServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    void makeBid() {

        userRepository.deleteAll();
        productRepository.deleteAll();

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("test");
        userRepository.save(dbUser);
        DBUser opt = userRepository.findByName("test").get();
        assertEquals(opt.getCoins(), 100);


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

        StoreService storeService = new StoreService(productRepository, userRepository,null);

        value = storeService.makeBid("3", 1, 0);
        Assertions.assertEquals(value, false);
        value = storeService.makeBid("3", 1, 11);
        Assertions.assertEquals(value, false);

        value = storeService.makeBid("2", 1, 11);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 89);

        value = storeService.makeBid("2", 1, 11);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 89);

        value = storeService.makeBid("2", 1, 12);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 88);

        value = storeService.makeBid("3", 1, 100);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 100);

        value = storeService.makeBid("1", 1, 10);
        Assertions.assertEquals(value, false);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 100);

        value = storeService.makeBid("1", 1, 15);
        Assertions.assertEquals(value, true);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 85);

        Assertions.assertEquals(productRepository.findById(1L).get().getHighestBid(), 15);

    }

    @Test
    void endBids() {

        userRepository.deleteAll();
        productRepository.deleteAll();

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        userRepository.save(dbUser);

        dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("2");
        userRepository.save(dbUser);

        dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("3");
        userRepository.save(dbUser);

        dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("4");
        userRepository.save(dbUser);

        dbUser = userRepository.findByName("4").get();

        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(10);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));

        dbUser.setProducts(List.of(dbProduct));
        userRepository.save(dbUser);

        StoreService storeService = new StoreService(productRepository, userRepository,null);

        long id = productRepository.findAll().get(0).getId();

        System.out.println(id);
        System.out.println(productRepository.findAll());

        Assertions.assertEquals(storeService.makeBid("1", id, 11), true);
        Assertions.assertEquals(storeService.makeBid("2", id, 12), true);
        Assertions.assertEquals(storeService.makeBid("2", id, 13), true);
        Assertions.assertEquals(storeService.makeBid("1", id, 14), true);
        Assertions.assertEquals(storeService.makeBid("4", id, 15), true);
        Assertions.assertEquals(storeService.makeBid("1", id, 16), true);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 84);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 87);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 85);
        Assertions.assertEquals(userRepository.findByName("4").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 0);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);

        storeService.endBids(UserServiceTest.parseDate("2022-05-06"));


        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getId(), 1);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);
        Assertions.assertEquals(productRepository.findById(id).get().isOnSale(), false);

        Assertions.assertEquals(userRepository.findByName("3").get().getProducts().size(), 0);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 84);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 100);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 116);
        Assertions.assertEquals(userRepository.findByName("4").get().getCoins(), 100);
        Assertions.assertEquals(productRepository.findById(1L).get().getBids().size(), 0);

//        dbProduct.setBids(new ArrayList<>());

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