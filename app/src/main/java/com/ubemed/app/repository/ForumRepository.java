package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@org.springframework.stereotype.Repository
public interface ForumRepository extends JpaRepository<DBPost, Long> {
//    @Query(value = "SELECT * FROM public.dbpost WHERE GAME_INFO = :id ", nativeQuery = true)
//    List<DBPost> findById(Long id);

}

