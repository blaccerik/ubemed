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
public class DBProduct {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBWheelGameEntry dbWheelGameEntry;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<DBStoreCats> dbStoreCats = new ArrayList<>();

    private long price;
    private long highestBid;
    private String title;
    private Date date;
    private long numberOfBids;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBProductState dbProductState;

    @OneToOne(mappedBy = "dbProduct", cascade = CascadeType.ALL)
    private DBStoreImage dbStoreImage;

    @OneToMany(mappedBy = "dbProduct", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DBBid> bids = new ArrayList<>();

    public void removeBid(DBBid dbBid) {
        bids.remove(dbBid);
        dbBid.setDbUser(null);
    }
}
