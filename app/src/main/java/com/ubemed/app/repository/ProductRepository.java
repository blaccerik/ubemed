package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ProductRepository extends JpaRepository<DBProduct, Long> {
//    Optional<DBStoreImage> findByName(String name);
}
