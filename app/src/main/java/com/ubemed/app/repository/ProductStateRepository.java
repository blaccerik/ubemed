package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBProductState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface ProductStateRepository extends JpaRepository<DBProductState, Long> {

    DBProductState findByState(DBProductState.states state);
}
