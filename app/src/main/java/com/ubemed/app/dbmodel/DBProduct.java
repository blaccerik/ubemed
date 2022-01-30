package com.ubemed.app.dbmodel;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBProduct {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;

    private long cost;
    private String title;
    @Column(length = 1000)
    private String description;

    @OneToOne(mappedBy = "dbProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DBStoreImage dbStoreImage;
}
