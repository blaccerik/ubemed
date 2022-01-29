package com.ubemed.app.service;

import com.ubemed.app.dbmodel.DBPost;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.dbmodel.DBVote;
import com.ubemed.app.repository.ForumRepository;
import com.ubemed.app.repository.UserRepository;
import com.ubemed.app.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    VoteRepository voteRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ForumRepository forumRepository;

    public boolean vote(long postId, String username, boolean isUpvote) {
        Optional<DBUser> optionalDBUser = userRepository.findByName(username);
        if (optionalDBUser.isEmpty()) {
            return false;
        }
        DBUser dbUser = optionalDBUser.get();

        Optional<DBPost> optionalDBPost = forumRepository.findById(postId);
        if (optionalDBPost.isEmpty()) {
            return false;
        }
        DBPost dbPost = optionalDBPost.get();

        Optional<DBVote> optionalDBVote = dbPost.getVotes().stream()
                .filter(p -> p.getDbUser().getId() == dbUser.getId()).findFirst();
        DBVote dbVote;
        if (optionalDBVote.isEmpty()) {
            dbVote = new DBVote();
            if (isUpvote) {
                dbVote.setAction(1);
                dbPost.setTotalVotes(dbPost.getTotalVotes() + 1);
            } else {
                dbVote.setAction(-1);
                dbPost.setTotalVotes(dbPost.getTotalVotes() - 1);
            }
            dbVote.setDbUser(dbUser);
            dbPost.getVotes().add(dbVote);
        } else {
            dbVote = optionalDBVote.get();
            int action = dbVote.getAction();
            if ((isUpvote && action == 1) || (!isUpvote && action == -1)) {
                dbPost.setTotalVotes(dbPost.getTotalVotes() - action);
                dbPost.getVotes().remove(dbVote);
            } else {
                dbVote.setAction(-action);
                dbPost.setTotalVotes(dbPost.getTotalVotes()- 2 * action);
            }
        }
        forumRepository.save(dbPost);
        return true;
    }
}
