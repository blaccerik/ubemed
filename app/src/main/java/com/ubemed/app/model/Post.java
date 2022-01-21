package com.ubemed.app.model;

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

    public Post(DBPost dbPost) {
        this.msg = dbPost.getMsg();
        this.id = dbPost.getId();
        this.author = dbPost.getAuthor();
        this.title = dbPost.getTitle();
        this.votes = dbPost.getVotes();
    }
}
