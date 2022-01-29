package com.ubemed.app.dbmodel;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Address;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBVote {

//
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "action")
    private int action;
//    @Column(name = "user_id")
//    private long user_id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;

//    @ManyToOne(fetch = FetchType.LAZY)
//    private DBPost dbPost;


//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private DBUser dbUser;
//
//    public DBVote(long user_id, long post_id, int action) {
////        this.user_id = user_id;
////        this.post_id = post_id;
//        this.action = action;
//    }
}
