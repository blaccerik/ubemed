package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBWheelGame;
import com.ubemed.app.model.WheelWinner;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.ProductStateRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.WheelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
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

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = cal.getTime();

        DBWheelGame dbWheelGame = new DBWheelGame();
        dbWheelGame.setValue(10);
        dbWheelGame.setCreateTime(d);
        wheelRepository.save(dbWheelGame);

        DBProduct product = null;
        for (DBProduct dbProduct1 : productRepository.findAll()) {
            if (dbProduct1.getTitle().equals("1")) {
                product = dbProduct1;
            }
        }

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND,1);
        d = cal.getTime();

        assertEquals(casinoService.enter("1", List.of(product.getId()), 6, d), 26);
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

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        d = cal.getTime();

        assertEquals(wheelRepository.findAll().get(0).getCreateTime().getTime(), d.getTime());
    }

    @Test
    void spin() {
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
        dbProduct.setTitle("11");
        dbProduct.setDbUser(dbUser);
        dbUser.getProducts().add(dbProduct);

        DBProduct dbProduct2 = new DBProduct();
        dbProduct2.setPrice(20);
        dbProduct2.setDbProductState(productStateRepository.findByState(DBProductState.states.inventory));
        dbProduct2.setTitle("12");
        dbProduct2.setDbUser(dbUser);
        dbUser.getProducts().add(dbProduct2);

        userRepository.save(dbUser);

        DBUser dbUser2 = new DBUser();
        dbUser2.setName("2");
        dbUser2.setCoins(10);

        DBProduct dbProduct3 = new DBProduct();
        dbProduct3.setPrice(20);
        dbProduct3.setDbProductState(productStateRepository.findByState(DBProductState.states.inventory));
        dbProduct3.setTitle("13");
        dbProduct3.setDbUser(dbUser2);
        dbUser2.getProducts().add(dbProduct3);

        DBProduct dbProduct4 = new DBProduct();
        dbProduct4.setPrice(20);
        dbProduct4.setDbProductState(productStateRepository.findByState(DBProductState.states.inventory));
        dbProduct4.setTitle("14");
        dbProduct4.setDbUser(dbUser2);
        dbUser2.getProducts().add(dbProduct4);

        userRepository.save(dbUser2);

        long id1 = userRepository.findByName("1").get().getProducts()
                .stream().filter(o -> o.getTitle().equals("11")).findFirst().get().getId();
        long id2 = userRepository.findByName("2").get().getProducts()
                .stream().filter(o -> o.getTitle().equals("13")).findFirst().get().getId();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 0);
        Date d = cal.getTime();

        DBWheelGame dbWheelGame = new DBWheelGame();
        dbWheelGame.setValue(0);
        dbWheelGame.setCreateTime(d);
        wheelRepository.save(dbWheelGame);

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 1);
        d = cal.getTime();

        assertEquals(casinoService.enter("1", List.of(id1), 5, d), 25);
        assertEquals(casinoService.enter("2", List.of(id2), 10, d), 30);


        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,31);
        d = cal.getTime();

        WheelWinner wheelWinner = casinoService.spin(d, 0.4);
        assertEquals(wheelWinner.getDbUser().getName(), "1");
        assertEquals(wheelWinner.getCoins(), 15);
        assertEquals(wheelWinner.getValue(), 55);
        assertEquals(wheelWinner.getList().size(), 2);
        assertEquals(wheelWinner.getList().stream().anyMatch(o -> o.getTitle().equals("11")), true);
        assertEquals(wheelWinner.getList().stream().anyMatch(o -> o.getTitle().equals("13")), true);


        DBUser dbUser1 = userRepository.findByName("1").get();
        assertEquals(dbUser1.getProducts().size(), 3);
        assertEquals(dbUser1.getProducts().stream().anyMatch(o -> o.getTitle().equals("11")), true);
        assertEquals(dbUser1.getProducts().stream().anyMatch(o -> o.getTitle().equals("12")), true);
        assertEquals(dbUser1.getProducts().stream().anyMatch(o -> o.getTitle().equals("13")), true);
        assertEquals(dbUser1.getCoins(), 20);

        DBUser dbUser22 = userRepository.findByName("2").get();
        assertEquals(dbUser22.getProducts().size(), 1);
        assertEquals(dbUser22.getProducts().stream().anyMatch(o -> o.getTitle().equals("14")), true);
        assertEquals(dbUser22.getCoins(), 0);

        DBProduct product = productRepository.findAll().stream().filter(o -> o.getTitle().equals("11")).findFirst().get();
        assertEquals(product.getDbProductState().getState(), DBProductState.states.inventory);
        assertEquals(product.getDbUser().getName(), "1");

        product = productRepository.findAll().stream().filter(o -> o.getTitle().equals("12")).findFirst().get();
        assertEquals(product.getDbProductState().getState(), DBProductState.states.inventory);
        assertEquals(product.getDbUser().getName(), "1");

        product = productRepository.findAll().stream().filter(o -> o.getTitle().equals("13")).findFirst().get();
        assertEquals(product.getDbProductState().getState(), DBProductState.states.inventory);
        assertEquals(product.getDbUser().getName(), "1");

        product = productRepository.findAll().stream().filter(o -> o.getTitle().equals("14")).findFirst().get();
        assertEquals(product.getDbProductState().getState(), DBProductState.states.inventory);
        assertEquals(product.getDbUser().getName(), "2");

        assertEquals(wheelRepository.findAll().size(), 1);

        dbWheelGame = wheelRepository.findAll().get(0);
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,31);
        d = cal.getTime();
        assertEquals(dbWheelGame.getCreateTime().getTime(), d.getTime());
        assertEquals(dbWheelGame.getDbWheelGameEntries().size(), 0);
        assertEquals(dbWheelGame.getValue(), 0);
    }

    @Test
    void spinNoWinner() {
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
        dbProduct.setTitle("11");
        dbProduct.setDbUser(dbUser);
        dbUser.getProducts().add(dbProduct);

        userRepository.save(dbUser);

        long id1 = userRepository.findByName("1").get().getProducts()
                .stream().filter(o -> o.getTitle().equals("11")).findFirst().get().getId();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        Date d = cal.getTime();

        DBWheelGame dbWheelGame = new DBWheelGame();
        dbWheelGame.setValue(0);
        dbWheelGame.setCreateTime(d);
        wheelRepository.save(dbWheelGame);


        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,31);
        d = cal.getTime();

        WheelWinner wheelWinner = casinoService.spin(d, 0.4);
        assertEquals(wheelWinner.getDbUser(), null);
        assertEquals(wheelWinner.getCoins(), 0);
        assertEquals(wheelWinner.getValue(), 0);
        assertEquals(wheelWinner.getList().size(), 0);


        DBUser dbUser1 = userRepository.findByName("1").get();
        assertEquals(dbUser1.getProducts().size(), 1);
        assertEquals(dbUser1.getProducts().stream().anyMatch(o -> o.getTitle().equals("11")), true);
        assertEquals(dbUser1.getCoins(), 10);

        DBProduct product = productRepository.findAll().stream().filter(o -> o.getTitle().equals("11")).findFirst().get();
        assertEquals(product.getDbProductState().getState(), DBProductState.states.inventory);
        assertEquals(product.getDbUser().getName(), "1");

        assertEquals(wheelRepository.findAll().size(), 1);

        dbWheelGame = wheelRepository.findAll().get(0);
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,31);
        d = cal.getTime();
        assertEquals(dbWheelGame.getCreateTime().getTime(), d.getTime());
        assertEquals(dbWheelGame.getDbWheelGameEntries().size(), 0);
        assertEquals(dbWheelGame.getValue(), 0);
    }


    @Test
    void spinCantEnter() {
        userRepository.deleteAll();
        wheelRepository.deleteAll();
        productRepository.deleteAll();

        CasinoService casinoService = new CasinoService(userRepository, wheelRepository, productStateRepository);

        DBUser dbUser = new DBUser();
        dbUser.setName("1");
        dbUser.setCoins(100);
        userRepository.save(dbUser);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date d = cal.getTime();

        DBWheelGame dbWheelGame = new DBWheelGame();
        dbWheelGame.setValue(0);
        dbWheelGame.setCreateTime(d);
        wheelRepository.save(dbWheelGame);

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        d = cal.getTime();
        assertEquals(casinoService.enter("1", List.of(), 5, d), -1);
        assertEquals(userRepository.findByName("1").get().getCoins(), 100);

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);
        d = cal.getTime();
        assertEquals(casinoService.enter("1", List.of(), 5, d), 5);
        assertEquals(userRepository.findByName("1").get().getCoins(), 95);

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 55);
        cal.set(Calendar.MILLISECOND, 0);
        d = cal.getTime();
        assertEquals(casinoService.enter("1", List.of(), 5, d), 5);
        assertEquals(userRepository.findByName("1").get().getCoins(), 90);

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,30);
        cal.set(Calendar.SECOND, 56);
        cal.set(Calendar.MILLISECOND, 0);
        d = cal.getTime();
        assertEquals(casinoService.enter("1", List.of(), 5, d), -1);
        assertEquals(userRepository.findByName("1").get().getCoins(), 90);

        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,31);
        cal.set(Calendar.SECOND, 35);
        d = cal.getTime();

        WheelWinner wheelWinner = casinoService.spin(d, 1);
        assertEquals(wheelWinner.getDbUser().getName(), "1");
        assertEquals(wheelWinner.getCoins(), 10);
        assertEquals(wheelWinner.getValue(), 10);
        assertEquals(wheelWinner.getList().size(), 0);
        assertEquals(userRepository.findByName("1").get().getCoins(), 100);
    }
}