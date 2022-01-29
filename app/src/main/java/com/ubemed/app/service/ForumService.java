package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBPost;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBVote;
import com.ubemed.app.model.Post;
import com.ubemed.app.repository.ForumRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Service
public class ForumService {

    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;

    private Stream<DBPost> getFromRepo() {
        return forumRepository.findAll().stream();
    }

    public List<Post> findAll() {
        return getFromRepo().map(dbPost -> new Post(dbPost)).collect(Collectors.toList());
    }

    public List<Post> findAllAuth(String username) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return findAll();
        }
        DBUser dbUser = optionalDBUser.get();
        return getFromRepo().map(
                dbPost -> this.map(dbPost, dbUser)
        ).collect(Collectors.toList());
    }

    private Post map(DBPost dbPost, DBUser dbUser) {
        Optional<DBVote> optionalDBVote = dbPost.getVotes().stream()
                .filter(p -> p.getDbUser().getId() == dbUser.getId()).findFirst();

        if (optionalDBVote.isEmpty()) {
            return new Post(dbPost);
        }

        int action = optionalDBVote.get().getAction();
        if (action > 0) {
            return new Post(dbPost, Post.vote.upvote);
        }
        return new Post(dbPost, Post.vote.downvote);
    }

    public void add(Post post) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(post.getAuthor());
        if (optionalDBUser.isPresent()) {
            DBUser dbUser = optionalDBUser.get();

            DBPost dbPost = new DBPost();
            dbPost.setTotalVotes(0);
            dbPost.setMsg(post.getMsg());
            dbPost.setTitle(post.getTitle());
            dbPost.setDbUser(dbUser);

            dbUser.getPosts().add(dbPost);
            userRepository.save(dbUser);
        }
    }

    public Post find(Long id) {
        return forumRepository.findById(id).map(dbPost -> new Post(dbPost)).orElse(null);
    }

    public Post findAuth(Long id, String username) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return find(id);
        }
        DBUser dbUser = optionalDBUser.get();
        return forumRepository.findById(id).map(
                dbPost -> this.map(dbPost, dbUser)
        ).orElse(null);
    }

}
