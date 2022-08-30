package com.ubemed.app.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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
    @Column(name = "totalVotes")
    private int totalVotes;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;

    @OneToMany(targetEntity = DBVote.class, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "votes", referencedColumnName = "id")
    private List<DBVote> votes;
}
