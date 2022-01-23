package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBPost;
import com.ubemed.app.model.Post;
import com.ubemed.app.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ForumService {

    @Autowired
    private ForumRepository forumRepository;


    public List<Post> findAll() {
        return forumRepository.findAll().stream().map(dbPost -> new Post(dbPost)).collect(Collectors.toList());
    }

    public void add(Post post) {
        forumRepository.save(new DBPost(post));
    }

    public Post find(Long id) {
        return forumRepository.findById(id).map(dbPost -> new Post(dbPost)).orElse(null);
    }

}
