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
  private String name;
  private String pass;
  private String role;
  private long coins;

  @OneToMany(mappedBy = "dbUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DBPost> posts;

  @OneToMany(mappedBy = "dbUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DBProduct> products;

  public DBUser(String name, String pass, roles role, long coins) {
    this.role = role.toString();
    this.name = name;
    this.pass = pass;
    this.coins = coins;
  }
}
