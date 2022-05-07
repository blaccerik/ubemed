package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBWheelGame;
import com.ubemed.app.dbmodel.DBWheelGameEntry;
import com.ubemed.app.model.WheelData;
import com.ubemed.app.model.WheelEnterBroadcast;
import com.ubemed.app.model.WheelWinner;
import com.ubemed.app.repository.ProductStateRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.WheelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@org.springframework.stereotype.Service
public class CasinoService {

    private final UserRepository userRepository;
    private final WheelRepository wheelRepository;
    private final ProductStateRepository productStateRepository;

    private static long mid = 0;
    private static List<WheelEnterBroadcast> list = new ArrayList<>();

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    public CasinoService(UserRepository userRepository, WheelRepository wheelRepository, ProductStateRepository productStateRepository) {
        this.userRepository = userRepository;
        this.wheelRepository = wheelRepository;
        this.productStateRepository = productStateRepository;
    }

    private DBWheelGame getLatestGame() {
        List<DBWheelGame> list = wheelRepository.findAll();
        if (list.isEmpty()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return list.get(0);
    }

    @Transactional
    public long enter(String username, List<Long> items, long coins, Date date) {

        DBWheelGame dbWheelGame = getLatestGame();
        Date start = dbWheelGame.getCreateTime();
        long secs = (date.getTime() - start.getTime()) / 1000;
        if (secs < 5 || secs > 55) {
            return -1;
        }

        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return -1;
        }
        DBUser dbUser = optionalDBUser.get();
        if (dbUser.getCoins() < coins) {
            return -1;
        }

        List<DBProduct> products = dbUser.getProducts();
        List<DBProduct> remove = new ArrayList<>();
        boolean missing;
        long value = coins;
        for (Long item : items) {
            missing = true;
            for (int i = 0; i < products.size(); i++) {
                DBProduct dbProduct = products.get(i);
                if (item == dbProduct.getId()) {
                    if (!dbProduct.getDbProductState().getState().equals(DBProductState.states.inventory)) {
                        return -1;
                    }
                    value += dbProduct.getPrice();
                    dbProduct.setDbUser(null);
                    DBProductState dbProductState = productStateRepository.findByState(DBProductState.states.casino);
                    dbProduct.setDbProductState(dbProductState);
                    remove.add(products.remove(i));
                    missing = false;
                    break;
                }
            }
            if (missing) {
                return -1;
            }
        }
        if (value == 0) {
            return -1;
        }

        DBWheelGameEntry dbWheelGameEntry = new DBWheelGameEntry();
        dbWheelGameEntry.setProducts(remove);
        dbWheelGameEntry.setDbUser(dbUser);
        dbWheelGameEntry.setCoins(coins);
        dbWheelGameEntry.setValue(value);

        dbWheelGame.addEntry(dbWheelGameEntry);
        dbWheelGame.setValue(dbWheelGame.getValue() + value);


        for (DBProduct dbProduct : remove) {
            dbProduct.setDbWheelGameEntry(dbWheelGameEntry);
        }

        dbUser.setCoins(dbUser.getCoins() - coins);

        wheelRepository.save(dbWheelGame);
        userRepository.save(dbUser);
        return value;
    }

    @Transactional
    public WheelWinner spin(Date date, double f) {
        DBWheelGame dbWheelGame = getLatestGame();
        long value = dbWheelGame.getValue();
        List<DBWheelGameEntry> list = dbWheelGame.getDbWheelGameEntries();

        double value2 = 0;
        DBUser dbUser = null;
        for (DBWheelGameEntry dbWheelGameEntry : list) {
            value2 += (double) dbWheelGameEntry.getValue() / (double) value;
            if (value2 >= f) {
                dbUser = dbWheelGameEntry.getDbUser();
                break;
            }
        }
        WheelWinner wheelWinner = new WheelWinner();
        wheelWinner.setDbUser(dbUser);
        wheelWinner.setList(new ArrayList<>());

        DBProductState inv = productStateRepository.findByState(DBProductState.states.inventory);

        for (DBWheelGameEntry dbWheelGameEntry : list) {
            for (DBProduct dbProduct : dbWheelGameEntry.getProducts()) {

                dbProduct.setDbUser(dbUser);
                dbProduct.setDbProductState(inv);
                dbProduct.setDbWheelGameEntry(null);
                dbUser.getProducts().add(dbProduct);
                wheelWinner.getList().add(dbProduct);
            }
            wheelWinner.setCoins(wheelWinner.getCoins() + dbWheelGameEntry.getCoins());
            dbWheelGameEntry.setProducts(new ArrayList<>());
            dbWheelGameEntry.setDbUser(null);
            dbWheelGameEntry.setDbWheelGame(null);
            dbUser.setCoins(dbUser.getCoins() + dbWheelGameEntry.getCoins());
        }
        wheelWinner.setValue(dbWheelGame.getValue());

        if (dbUser != null) {
            userRepository.save(dbUser);
        }
        wheelRepository.delete(dbWheelGame);

        DBWheelGame dbWheelGame1 = new DBWheelGame();
        dbWheelGame1.setCreateTime(date);
        dbWheelGame1.setValue(0);
        dbWheelGame1.setDbWheelGameEntries(new ArrayList<>());
        wheelRepository.save(dbWheelGame1);
        return wheelWinner;
    }


    public WheelData getData() {
        DBWheelGame dbWheelGame = getLatestGame();
        WheelData wheelData = new WheelData();
        wheelData.setDate(dbWheelGame.getCreateTime());
        wheelData.setValue(dbWheelGame.getValue());
        wheelData.setList(list);
        return wheelData;
    }

    public void reset() {
        mid = 0;
        list.clear();
    }

    public void update(String username, long coins, long value) {
        mid += 1;
        WheelEnterBroadcast wheelEnterBroadcast = new WheelEnterBroadcast(mid, username, coins, value);
        list.add(wheelEnterBroadcast);
        template.convertAndSend("/casino", wheelEnterBroadcast);
    }
}
