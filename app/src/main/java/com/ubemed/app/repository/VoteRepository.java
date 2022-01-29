package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface VoteRepository extends JpaRepository<DBVote, Long> {
}
