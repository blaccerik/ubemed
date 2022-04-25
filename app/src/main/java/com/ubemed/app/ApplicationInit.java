package com.ubemed.app;

import com.ubemed.app.dbmodel.DBStoreCats;
import com.ubemed.app.repository.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationInit implements CommandLineRunner {

    @Autowired
    private CatRepository catRepository;

    @Override
    public void run(String... args) {
        List<DBStoreCats> list = catRepository.findAll();
        if (list.isEmpty()) {
            List<DBStoreCats> catsList = List.of(
                    new DBStoreCats(1,"cat 1"),
                    new DBStoreCats(2,"cat 2"),
                    new DBStoreCats(3,"cat 3"),
                    new DBStoreCats(4,"cat 4"),
                    new DBStoreCats(5,"cat 5"),
                    new DBStoreCats(6,"cat 6")
            );
            catRepository.saveAll(catsList);
        }
    }

}
