package com.ubemed.app.controller;

import com.ubemed.app.config.JwtTokenUtil;
import com.ubemed.app.model.Post;
import com.ubemed.app.model.Vote;
import com.ubemed.app.service.ForumService;
import com.ubemed.app.service.UserService;
import com.ubemed.app.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/forum")
@RestController
public class ForumController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ForumService forumService;
    @Autowired
    private VoteService voteService;

    @GetMapping()
    public List<Post> findAll(
    ) {
        return forumService.findAll();
    }

    @PostMapping("/new")
    public void add(
        @RequestHeader(name="Authorization") String token,
        @RequestBody Post post
    ) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        post.setAuthor(username);
        post.setVotes(0);
        forumService.add(post);
    }

    @PutMapping("/{postId}")
    public boolean vote(
            @PathVariable Long postId,
            @RequestHeader(name="Authorization") String token,
            @RequestBody Vote vote
            ) {

        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        return voteService.vote(postId, username, vote.isUpvote());
    }

    @GetMapping("/{id}")
    public Post find(@PathVariable Long id) {
        return forumService.find(id);
    }
}
