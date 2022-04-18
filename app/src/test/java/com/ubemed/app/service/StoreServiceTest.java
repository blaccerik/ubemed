package com.ubemed.app.service;


import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.BidRepository;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SpringBootTest
@Transactional
public class StoreServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CatRepository catRepository;

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

        StoreService storeService = new StoreService(productRepository, userRepository,null);

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
        userRepository.save(dbUser);

        DBUser dbUser2 = new DBUser();
        dbUser2.setCoins(100);
        dbUser2.setName("2");
        userRepository.save(dbUser2);

        DBUser dbUser3 = new DBUser();
        dbUser3.setCoins(100);
        dbUser3.setName("3");
        userRepository.save(dbUser3);

        DBUser dbUser4 = new DBUser();
        dbUser4.setCoins(100);
        dbUser4.setName("4");
        userRepository.save(dbUser4);

        DBUser dbUser5 = userRepository.findByName("4").get();

        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser5);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(100);
        dbProduct.setHighestBid(10);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));

        dbUser5.getProducts().add(dbProduct);
        userRepository.save(dbUser5);

        StoreService storeService = new StoreService(productRepository, userRepository,null);

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
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 100);

        storeService.endBids(UserServiceTest.parseDate("2022-05-06"));

        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 16);
        Assertions.assertEquals(productRepository.findById(id).get().isOnSale(), false);

        Assertions.assertEquals(userRepository.findByName("4").get().getProducts().size(), 0);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 84);
        Assertions.assertEquals(userRepository.findByName("2").get().getCoins(), 100);
        Assertions.assertEquals(userRepository.findByName("3").get().getCoins(), 100);
        Assertions.assertEquals(userRepository.findByName("4").get().getCoins(), 116);
        Assertions.assertEquals(productRepository.findById(id).get().getBids().size(), 0);
    }

    @Test
    void endBidsNoBids() {

        userRepository.deleteAll();
        productRepository.deleteAll();
        StoreService storeService = new StoreService(productRepository, userRepository,null);

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(20);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));
        dbUser.getProducts().add(dbProduct);

        userRepository.save(dbUser);

        long id = productRepository.findAll().get(0).getId();


        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        Assertions.assertEquals(productRepository.findById(id).get().isOnSale(), true);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 20);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);

        storeService.endBids(UserServiceTest.parseDate("2022-05-06"));

        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        Assertions.assertEquals(productRepository.findById(id).get().isOnSale(), false);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 10);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 110);
    }

    @Test
    void sell() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        StoreService storeService = new StoreService(productRepository, userRepository,null);

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser);
        dbProduct.setOnSale(false);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(15);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));
        dbUser.getProducts().add(dbProduct);

        userRepository.save(dbUser);
        long id = productRepository.findAll().get(0).getId();
        Assertions.assertEquals(storeService.sell("1", id, 20, UserServiceTest.parseDate("2022-05-06")), true);

        Assertions.assertEquals(productRepository.findById(id).get().getPrice(), 10);
        Assertions.assertEquals(productRepository.findById(id).get().getBids().size(), 0);
        Assertions.assertEquals(productRepository.findById(id).get().getHighestBid(), 20);
        Assertions.assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        Assertions.assertEquals(productRepository.findById(id).get().getDate().getTime(), UserServiceTest.parseDate("2022-05-06").getTime());
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        Assertions.assertEquals(userRepository.findByName("1").get().getProducts().get(0).getId(), id);
        Assertions.assertEquals(userRepository.findByName("1").get().getCoins(), 80);
    }

    @Test
    void sellFailed() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        StoreService storeService = new StoreService(productRepository, userRepository,null);

        DBUser dbUser = new DBUser();
        dbUser.setCoins(100);
        dbUser.setName("1");
        DBProduct dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser);
        dbProduct.setOnSale(false);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(15);
        dbProduct.setBids(new ArrayList<>());
        dbProduct.setDate(UserServiceTest.parseDate("2022-05-05"));
        dbUser.getProducts().add(dbProduct);
        userRepository.save(dbUser);

        DBUser dbUser2 = new DBUser();
        dbUser2.setCoins(100);
        dbUser2.setName("2");
        DBProduct dbProduct2 = new DBProduct();
        dbProduct2.setDbUser(dbUser2);
        dbProduct2.setOnSale(false);
        dbProduct2.setPrice(10);
        dbProduct2.setHighestBid(15);
        dbProduct2.setBids(new ArrayList<>());
        dbProduct2.setDate(UserServiceTest.parseDate("2022-05-05"));
        dbUser2.getProducts().add(dbProduct2);
        userRepository.save(dbUser2);

        long id;
        long id2;
        DBProduct dbProduct1 = productRepository.findAll().get(0);
        DBProduct dbProduct11 = productRepository.findAll().get(1);
        if (dbProduct1.getDbUser().getName().equals("1")) {
            id = dbProduct1.getId();
            id2 = dbProduct11.getId();
        } else {
            id = dbProduct11.getId();
            id2 = dbProduct1.getId();
        }
        Assertions.assertEquals(storeService.sell("1", id2, 20, UserServiceTest.parseDate("2022-05-06")), false);
        Assertions.assertEquals(storeService.sell("1", id, 101, UserServiceTest.parseDate("2022-05-06")), false);

        DBProduct dbProduct3 = productRepository.getById(id);
        dbProduct3.setOnSale(true);
        productRepository.save(dbProduct3);

        Assertions.assertEquals(storeService.sell("1", id, 20, UserServiceTest.parseDate("2022-05-06")), false);
    }

    @Test
    void save() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        StoreService storeService = new StoreService(productRepository, userRepository,catRepository);

        DBUser dbUser = new DBUser();
        dbUser.setCoins(20);
        dbUser.setName("1");
        userRepository.save(dbUser);
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return "image/png";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return "test".getBytes();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        assertEquals(storeService.save("1", "test", List.of(1L,3L,6L), 20, multipartFile, UserServiceTest.parseDate("2022-05-05"), StoreService.strategy.test), true);
        assertEquals(userRepository.findByName("1").get().getCoins(), 0);
        assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);

        long id = productRepository.findAll().get(0).getId();
        assertEquals(productRepository.findById(id).get().getDbUser().getName(), "1");
        assertEquals(productRepository.findById(id).get().getDbStoreCats().size(), 3);
        assertEquals(productRepository.findById(id).get().isOnSale(), false);
        assertEquals(productRepository.findById(id).get().getTitle(), "test");
        assertEquals(productRepository.findById(id).get().getPrice(), 20);
        assertEquals(productRepository.findById(id).get().getHighestBid(), 20);
        assertNotEquals(productRepository.findById(id).get().getDbStoreImage(), null);
        assertEquals(productRepository.findById(id).get().getDate().getTime(), UserServiceTest.parseDate("2022-05-05").getTime());

    }
}