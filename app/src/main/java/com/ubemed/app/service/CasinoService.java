package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBProductState;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBWheelGame;
import com.ubemed.app.dbmodel.DBWheelGameEntry;
import com.ubemed.app.repository.ProductStateRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.WheelRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CasinoService {

    private final UserRepository userRepository;
    private final WheelRepository wheelRepository;

    private final ProductStateRepository productStateRepository;

    @Autowired
    public CasinoService(UserRepository userRepository, WheelRepository wheelRepository, ProductStateRepository productStateRepository) {
        this.userRepository = userRepository;
        this.wheelRepository = wheelRepository;
        this.productStateRepository = productStateRepository;
    }

    private DBWheelGame getLatestGame() {
        List<DBWheelGame> list = wheelRepository.findAll();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public long enter(String username, List<Long> items, long coins) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return -1;
        }
        DBUser dbUser = optionalDBUser.get();
        if (dbUser.getCoins() < coins) {
            return -1;
        }

        DBWheelGame dbWheelGame = getLatestGame();
        if (dbWheelGame == null) {
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

    public void spin(Date date) {
        DBWheelGame dbWheelGame = getLatestGame();

        // if no game then create new
        if (dbWheelGame == null) {
            dbWheelGame = new DBWheelGame();
            dbWheelGame.setCreateTime(date);
            dbWheelGame.setValue(0);
        } else {
            long value = dbWheelGame.getValue();
            List<DBWheelGameEntry> list = dbWheelGame.getDbWheelGameEntries();
        }
    }
}
