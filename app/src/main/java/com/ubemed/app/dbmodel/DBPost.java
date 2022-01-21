package com.ubemed.app.dbmodel;

import com.ubemed.app.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "msg", length = 2000)
    private String msg;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "votes")
    private int votes;

    public DBPost(Post post) {
        this.author = post.getAuthor();
        this.title = post.getTitle();
        this.msg = post.getMsg();
        this.votes = 0;
    }
}
