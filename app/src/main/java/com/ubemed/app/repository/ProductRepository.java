package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ProductRepository extends JpaRepository<DBProduct, Long> {
//    Optional<DBStoreImage> findByName(String name);

    @Query(value = "SELECT * FROM public.dbproduct WHERE on_sale=false AND db_user_id=:id", nativeQuery = true)
    List<DBProduct> findAllById(long id);
}
