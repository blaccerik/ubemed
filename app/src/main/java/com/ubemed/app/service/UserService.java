package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

@org.springframework.stereotype.Service
public class UserService {

    private static final long startCoins = 0;
    private static final long dailyCoins = 100;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public long getCoins(String name) {
        Optional<DBUser> optional = userRepository.findByName(name);
        if (optional.isEmpty()) {
            return -1;
        }
        return optional.get().getCoins();
    }

    public boolean claim(String name, Date date) {
        Optional<DBUser> optional = userRepository.findByName(name);
        if (optional.isEmpty()) {
            return false;
        }
        DBUser dbUser = optional.get();
        long secs = (date.getTime() -  dbUser.getLastClaimDate().getTime()) / 1000;
        long hours = secs / 3600;
        if (hours >= 24) {
            dbUser.setLastClaimDate(date);
            dbUser.setCoins(dbUser.getCoins() + dailyCoins);
            userRepository.save(dbUser);
            return true;
        }
        return false;
    }

    public boolean save(String name, String pass) {
        Optional<DBUser> optional = userRepository.findByName(name);
        if (optional.isEmpty()) {
            DBUser dbUser = new DBUser(name, pass, DBUser.roles.user, startCoins, new Date());
            userRepository.save(dbUser);
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
