package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void claim() {

        DBUser dbUser = new DBUser();
        dbUser.setName("test");
        dbUser.setCoins(0);
        dbUser.setLastClaimDate(parseDate("2022-04-05"));
        userRepository.save(dbUser);


        UserService userService = new UserService(userRepository);
        boolean value;
        assertEquals(dbUser.getCoins(), 0);
        value = userService.claim("test", parseDate("2022-04-04"));
        assertEquals(value, false);
        assertEquals(userRepository.findByName("test").get().getCoins(), 0);

        value = userService.claim("test", parseDate("2022-04-05"));
        assertEquals(value, false);
        assertEquals(userRepository.findByName("test").get().getCoins(), 0);

        value = userService.claim("test", parseDate("2022-04-06"));
        assertEquals(value, true);
        assertEquals(userRepository.findByName("test").get().getCoins(), 100);
        assertEquals(userRepository.findByName("test").get().getLastClaimDate().getTime(), parseDate("2022-04-06").getTime());

        value = userService.claim("test", parseDate("2022-04-06"));
        assertEquals(value, false);
        assertEquals(userRepository.findByName("test").get().getCoins(), 100);
    }
}