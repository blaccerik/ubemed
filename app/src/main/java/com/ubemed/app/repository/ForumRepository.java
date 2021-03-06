package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ForumRepository extends JpaRepository<DBPost, Long> {
}

