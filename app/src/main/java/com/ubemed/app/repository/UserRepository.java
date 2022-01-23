package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<DBUser, Long>{
//    @Query(value = "SELECT * FROM public.dbpost WHERE GAME_INFO = :id", nativeQuery = true)
    Optional<DBUser> findByName(String name);
}
