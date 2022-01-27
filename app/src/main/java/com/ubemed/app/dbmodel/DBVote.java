package com.ubemed.app.dbmodel;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Address;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Collection;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBVote {

//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id")
    private long user_id;
    @Column(name = "action")
    private int action;
    @Column(name = "post_id")
    private long post_id;

    public DBVote(long user_id, long post_id, int action) {
        this.user_id = user_id;
        this.post_id = post_id;
        this.action = action;
    }
}
