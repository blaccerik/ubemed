package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBVote;
import com.ubemed.app.dbmodel.DBWheelGame;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface WheelRepository extends JpaRepository<DBWheelGame, Long> {
}
