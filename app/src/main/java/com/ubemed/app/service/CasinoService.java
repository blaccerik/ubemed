package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBWheelGame;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.WheelRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CasinoService {

    private final UserRepository userRepository;
    private final WheelRepository wheelRepository;

    @Autowired
    public CasinoService(UserRepository userRepository, WheelRepository wheelRepository) {
        this.userRepository = userRepository;
        this.wheelRepository = wheelRepository;
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
        List<DBProduct> products = dbUser.getProducts();
        List<DBProduct> remove = new ArrayList<>();
        boolean missing;
        long value = coins;
        for (Long item : items) {
            missing = true;
            for (int i = 0; i < products.size(); i++) {
                DBProduct dbProduct = products.get(i);
                if (item == dbProduct.getId()) {
                    if (dbProduct.isOnSale()) {
                        return -1;
                    }
                    value += dbProduct.getPrice();
                    dbProduct.setDbUser(null);
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
        System.out.println(remove);
        System.out.println(products);
        DBWheelGame dbWheelGame = new DBWheelGame();
//        dbWheelGame.setProducts(remove);
//        wheelRepository.save(dbWheelGame);

        // todo fix

        userRepository.save(dbUser);
        return value;
    }
}
