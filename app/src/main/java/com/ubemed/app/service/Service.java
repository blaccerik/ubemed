package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBPost;
import com.ubemed.app.model.Post;
import com.ubemed.app.repository.Repository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private Repository repository;


    public List<Post> findAll() {
        return repository.findAll().stream().map(dbPost -> new Post(dbPost)).collect(Collectors.toList());
    }

    public void add(Post post) {
        repository.save(new DBPost(post));
    }

    public Post find(Long id) {
        return repository.findById(id).map(dbPost -> new Post(dbPost)).orElse(null);
    }

}