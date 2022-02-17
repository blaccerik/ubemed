package com.ubemed.app.repository;

import com.ubemed.app.dbmodel.DBStoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface ImageRepository extends JpaRepository<DBStoreImage, Long> {
}
