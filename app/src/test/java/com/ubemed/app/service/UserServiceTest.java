package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private static DBUser dbUser = new DBUser();

    @Test
    void claim() {

        UserRepository userRepository = new UserRepository() {
            @Override
            public Optional<DBUser> findByName(String name) {
                return Optional.of(dbUser);
            }

            @Override
            public List<DBUser> findAll() {
                return null;
            }

            @Override
            public List<DBUser> findAll(Sort sort) {
                return null;
            }

            @Override
            public List<DBUser> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public <S extends DBUser> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends DBUser> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends DBUser> List<S> saveAllAndFlush(Iterable<S> entities) {
                return null;
            }

            @Override
            public void deleteAllInBatch(Iterable<DBUser> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public DBUser getOne(Long aLong) {
                return null;
            }

            @Override
            public DBUser getById(Long aLong) {
                return null;
            }

            @Override
            public <S extends DBUser> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends DBUser> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public Page<DBUser> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends DBUser> S save(S entity) {
                dbUser = entity;
                return null;
            }

            @Override
            public Optional<DBUser> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(DBUser entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends DBUser> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends DBUser> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends DBUser> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends DBUser> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends DBUser> boolean exists(Example<S> example) {
                return false;
            }
        };

        UserService userService = new UserService(userRepository);
        boolean value;
        dbUser.setCoins(0);
        dbUser.setLastClaimDate(parseDate("2022-04-05"));
        assertEquals(dbUser.getCoins(), 0);
        value = userService.claim("test", parseDate("2022-04-04"));
        assertEquals(value, false);
        assertEquals(dbUser.getCoins(), 0);
        value = userService.claim("test", parseDate("2022-04-05"));
        assertEquals(value, false);
        assertEquals(dbUser.getCoins(), 0);
        value = userService.claim("test", parseDate("2022-04-06"));
        assertEquals(value, true);
        assertEquals(dbUser.getCoins(), 100);
        assertEquals(dbUser.getLastClaimDate().getTime(), parseDate("2022-04-06").getTime());
        value = userService.claim("test", parseDate("2022-04-06"));
        assertEquals(value, false);
        assertEquals(dbUser.getCoins(), 100);
    }
}