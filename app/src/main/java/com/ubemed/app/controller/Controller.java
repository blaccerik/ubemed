package com.ubemed.app.controller;

import com.ubemed.app.model.Post;
import com.ubemed.app.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/forum")
@RestController
public class Controller {

    @Autowired
    private Service service;

    @GetMapping()
    public List<Post> findAll(
    ) {
        return service.findAll();
    }

    @PostMapping("/new")
    public void add(@RequestBody Post post) {
        service.add(post);
    }

    @GetMapping("/{id}")
    public Post find(@PathVariable Long id) {
        return service.find(id);
    }
}
