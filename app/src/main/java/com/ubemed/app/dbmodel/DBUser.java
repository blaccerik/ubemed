package com.ubemed.app.dbmodel;

import com.ubemed.app.model.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBUser {

  public enum roles {
    user,
    admin
  }

  @Id
  @GeneratedValue
  @Column(name = "id")
  private long id;
  @Column(name = "name")
  private String name;
  @Column(name = "pass")
  private String pass;
  @Column(name = "role")
  private String role;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "coin_id")
  private DBCoin dbCoin;

//   votes made by user
//  @OneToMany(targetEntity = DBVote.class, cascade = CascadeType.ALL)
//  @JoinColumn(name = "user_vote", referencedColumnName = "id")
//  private List<DBVote> votes;

  // posts made by user
  @OneToMany(mappedBy = "dbUser", cascade = CascadeType.ALL, orphanRemoval = true)
//  @JoinColumn(name = "user_post", referencedColumnName = "id")
  private List<DBPost> posts;

  public DBUser(String name, String pass, roles role) {
    this.role = role.toString();
    this.name = name;
    this.pass = pass;
  }
}
