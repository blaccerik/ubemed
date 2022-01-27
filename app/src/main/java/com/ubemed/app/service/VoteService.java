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

        // find if has already voted
        Optional<DBVote> optionalDBVote = voteRepository.findBy(dbPost.getId(), dbUser.getId());
        DBVote dbVote;
        if (optionalDBVote.isEmpty()) {
            if (isUpvote) {
                dbVote = new DBVote(dbUser.getId(), dbPost.getId(), 1);
                dbPost.setVotes(dbPost.getVotes() + 1);
            } else {
                dbVote = new DBVote(dbUser.getId(), dbPost.getId(), -1);
                dbPost.setVotes(dbPost.getVotes() - 1);
            }
            forumRepository.save(dbPost);
            voteRepository.save(dbVote);
        } else {
            dbVote = optionalDBVote.get();
            int action = dbVote.getAction();
            if ((isUpvote && action == 1) || (!isUpvote && action == -1)) {
                voteRepository.delete(dbVote);
                dbPost.setVotes(dbPost.getVotes() - action);
            } else {
                dbVote.setAction(-action);
                voteRepository.save(dbVote);
                dbPost.setVotes(dbPost.getVotes() - 2 * action);
            }
            forumRepository.save(dbPost);
        }
        return true;
    }
}
