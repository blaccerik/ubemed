package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBPost;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<DBPost, Long> {

//    @Query(value = "SELECT * FROM public.dbpost WHERE GAME_INFO = :id ", nativeQuery = true)
//    List<DBPost> findById(Long id);

}

