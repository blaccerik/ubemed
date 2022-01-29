package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBCoin;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.CoinRepository;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@org.springframework.stereotype.Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CoinRepository coinRepository;

    public boolean save(String name, String pass) {
        Optional<DBUser> optional = userRepository.findByName(name);
        if (optional.isEmpty()) {
            DBUser dbUser = new DBUser(name, pass, DBUser.roles.user);
            DBCoin dbCoin = new DBCoin();
            dbCoin.setDbUser(dbUser);
            dbCoin.setCoins(0);
            dbUser.setDbCoin(dbCoin);
            userRepository.save(dbUser);
            coinRepository.save(dbCoin);
            return true;
        }
        return false;
    }


    public boolean checkPass(String name, String pass) {
        Optional<DBUser> optional = userRepository.findByName(name);
        if (optional.isPresent()) {
            DBUser dbUser = optional.get();
            // todo hash passwords
            return pass.equals(dbUser.getPass());
        }
        return false;
    }

}
