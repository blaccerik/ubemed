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
public class DBProduct {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<DBStoreCats> dbStoreCats;

    private long cost;
    private String title;

    @OneToOne(mappedBy = "dbProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DBStoreImage dbStoreImage;
}
