package com.ubemed.app.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
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
  private Date lastClaimDate;

  @OneToMany(mappedBy = "dbUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<DBPost> posts = new ArrayList<>();

  @OneToMany(mappedBy = "dbUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<DBProduct> products = new ArrayList<>();

  public DBUser(String name, String pass, roles role, long coins, Date date) {
    this.role = role.toString();
    this.name = name;
    this.pass = pass;
    this.coins = coins;
    this.lastClaimDate = date;
  }

  public void removeProduct(DBProduct dbProduct) {
    products.remove(dbProduct);
    dbProduct.setDbUser(null);
  }

//  public void addProduct(DBProduct dbProduct) {
//    products.add(dbProduct);
//    dbProduct.setDbUser(this);
//  }
}
