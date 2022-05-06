package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBWheelGame;
import com.ubemed.app.repository.CatRepository;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.ProductStateRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.WheelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CasinoServiceTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductStateRepository productStateRepository;

    @Autowired
    WheelRepository wheelRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    void enter() {

        userRepository.deleteAll();
        wheelRepository.deleteAll();
        productRepository.deleteAll();

        CasinoService casinoService = new CasinoService(userRepository, wheelRepository, productStateRepository);

        DBUser dbUser = new DBUser();
        dbUser.setName("1");
        dbUser.setCoins(10);

        DBProduct dbProduct = new DBProduct();
        dbProduct.setPrice(20);
        dbProduct.setDbProductState(productStateRepository.findByState(DBProductState.states.inventory));
        dbProduct.setTitle("1");
        dbProduct.setDbUser(dbUser);

        DBProduct dbProduct2 = new DBProduct();
        dbProduct2.setPrice(30);
        dbProduct2.setTitle("2");
        dbProduct2.setDbProductState(productStateRepository.findByState(DBProductState.states.inventory));
        dbProduct2.setDbUser(dbUser);

        dbUser.getProducts().add(dbProduct);
        dbUser.getProducts().add(dbProduct2);

        userRepository.save(dbUser);

        DBWheelGame dbWheelGame = new DBWheelGame();
        dbWheelGame.setValue(10);
        dbWheelGame.setCreateTime(UserServiceTest.parseDate("2022-05-05"));
        wheelRepository.save(dbWheelGame);

        DBProduct product = null;
        for (DBProduct dbProduct1 : productRepository.findAll()) {
            if (dbProduct1.getTitle().equals("1")) {
                product = dbProduct1;
            }
        }

        assertEquals(casinoService.enter("1", List.of(product.getId()), 6), 26);
        assertEquals(userRepository.findByName("1").get().getCoins(), 4);
        assertEquals(userRepository.findByName("1").get().getProducts().size(), 1);
        assertEquals(userRepository.findByName("1").get().getProducts().get(0).getTitle(), "2");

        assertEquals(productRepository.findById(product.getId()).get().getDbProductState().getState(), DBProductState.states.casino);

        assertEquals(wheelRepository.findAll().size(), 1);
        assertEquals(wheelRepository.findAll().get(0).getDbWheelGameEntries().size(), 1);
        assertEquals(wheelRepository.findAll().get(0).getDbWheelGameEntries().get(0).getDbUser().getName(), "1");
        assertEquals(wheelRepository.findAll().get(0).getDbWheelGameEntries().get(0).getCoins(), 6);
        assertEquals(wheelRepository.findAll().get(0).getDbWheelGameEntries().get(0).getValue(), 26);
        assertEquals(wheelRepository.findAll().get(0).getValue(), 36);
        assertEquals(wheelRepository.findAll().get(0).getCreateTime().getTime(), UserServiceTest.parseDate("2022-05-05").getTime());
    }
}