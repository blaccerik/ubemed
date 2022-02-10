package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBStoreCats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface CatRepository extends JpaRepository<DBStoreCats, Long> {

    Optional<DBStoreCats> findByCode(long code);

}
