package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.repository.ProductRepository;
import com.ubemed.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StoreServiceTest {

    private DBUser dbUser1;
    private DBUser dbUser2;
    private DBUser dbUser3;

    private DBProduct dbProduct;

    private UserRepository userRepository = new UserRepository() {
        @Override
        public Optional<DBUser> findByName(String name) {
            if (name.equals("1")) {
                return Optional.of(dbUser1);
            } else if (name.equals("2")) {
                return Optional.of(dbUser2);
            } else if (name.equals("3")) {
                return Optional.of(dbUser3);
            }
            return Optional.empty();
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
            if (entity.getId() == 1) {
                dbUser1 = entity;
            }
            if (entity.getId() == 2) {
                dbUser2 = entity;
            }
            if (entity.getId() == 3) {
                dbUser3 = entity;
            }
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

    private ProductRepository productRepository = new ProductRepository() {
        @Override
        public List<DBProduct> findAllById(long id) {
            return null;
        }

        @Override
        public List<DBProduct> findAll() {
            return null;
        }

        @Override
        public List<DBProduct> findAll(Sort sort) {
            return null;
        }

        @Override
        public List<DBProduct> findAllById(Iterable<Long> longs) {
            return null;
        }

        @Override
        public <S extends DBProduct> List<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public void flush() {

        }

        @Override
        public <S extends DBProduct> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends DBProduct> List<S> saveAllAndFlush(Iterable<S> entities) {
            return null;
        }

        @Override
        public void deleteAllInBatch(Iterable<DBProduct> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public DBProduct getOne(Long aLong) {
            return null;
        }

        @Override
        public DBProduct getById(Long aLong) {
            return null;
        }

        @Override
        public <S extends DBProduct> List<S> findAll(Example<S> example) {
            return null;
        }

        @Override
        public <S extends DBProduct> List<S> findAll(Example<S> example, Sort sort) {
            return null;
        }

        @Override
        public Page<DBProduct> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends DBProduct> S save(S entity) {
            return null;
        }

        @Override
        public Optional<DBProduct> findById(Long aLong) {
            return Optional.of(dbProduct);
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
        public void delete(DBProduct entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends DBProduct> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public <S extends DBProduct> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends DBProduct> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends DBProduct> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends DBProduct> boolean exists(Example<S> example) {
            return false;
        }
    };

    @Test
    void makeBid() {
        boolean value;
        dbUser1 = new DBUser();
        dbUser1.setId(1);
        dbUser1.setCoins(100);

        dbUser2 = new DBUser();
        dbUser2.setId(2);
        dbUser2.setCoins(100);

        dbUser3 = new DBUser();
        dbUser3.setId(3);
        dbUser3.setCoins(100);

        dbProduct = new DBProduct();
        dbProduct.setDbUser(dbUser3);
        dbProduct.setOnSale(true);
        dbProduct.setPrice(10);
        dbProduct.setHighestBid(10);
        dbProduct.setBids(new ArrayList<>());

        StoreService storeService = new StoreService(productRepository, userRepository,null);

        value = storeService.makeBid("3", 1, 0);
        assertEquals(value, false);
        value = storeService.makeBid("3", 1, 11);
        assertEquals(value, false);

        value = storeService.makeBid("2", 1, 11);
        assertEquals(value, true);
        assertEquals(dbUser2.getCoins(), 89);

        value = storeService.makeBid("2", 1, 11);
        assertEquals(value, false);
        assertEquals(dbUser2.getCoins(), 89);

        value = storeService.makeBid("2", 1, 12);
        assertEquals(value, true);
        assertEquals(dbUser2.getCoins(), 88);

        value = storeService.makeBid("3", 1, 100);
        assertEquals(value, false);
        assertEquals(dbUser3.getCoins(), 100);

        value = storeService.makeBid("1", 1, 10);
        assertEquals(value, false);
        assertEquals(dbUser1.getCoins(), 100);

        value = storeService.makeBid("1", 1, 15);
        assertEquals(value, true);
        assertEquals(dbUser1.getCoins(), 85);
        assertEquals(dbProduct.getHighestBid(), 15);
    }

    @Test
    void endBids() {
    }
}