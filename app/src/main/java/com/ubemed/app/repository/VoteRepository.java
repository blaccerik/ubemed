package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface VoteRepository extends JpaRepository<DBVote, Long> {

    @Query(value = "select * from public.dbvote where user_id=:user and post_id=:post", nativeQuery = true)
    Optional<DBVote> findBy(long post, long user);



}
