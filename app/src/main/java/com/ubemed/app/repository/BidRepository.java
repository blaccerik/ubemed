package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBBid;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface BidRepository extends JpaRepository<DBBid, Long> {

}
