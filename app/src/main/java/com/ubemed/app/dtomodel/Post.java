package com.ubemed.app.dtomodel;

import com.ubemed.app.dbmodel.DBPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Post {

    private String msg;
    private Long id;
    private int votes;
    private String author;
    private String title;
    private vote myVote;

    public enum vote {
        upvote,
        neutral,
        downvote
    }

    public Post(DBPost dbPost) {
        this.votes = dbPost.getTotalVotes();
        this.msg = dbPost.getMsg();
        this.title = dbPost.getTitle();
        this.id = dbPost.getId();
        this.author = dbPost.getDbUser().getName();
        this.myVote = vote.neutral;
    }

    public Post(DBPost dbPost, vote myVote) {
        this.votes = dbPost.getTotalVotes();
        this.msg = dbPost.getMsg();
        this.title = dbPost.getTitle();
        this.id = dbPost.getId();
        this.author = dbPost.getDbUser().getName();
        this.myVote = myVote;
    }
}
