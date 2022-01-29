package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBCoin;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface CoinRepository extends JpaRepository<DBCoin, Long> {
}
