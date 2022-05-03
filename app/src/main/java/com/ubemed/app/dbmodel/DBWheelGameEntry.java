package com.ubemed.app.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBWheelGameEntry {

    @Id
    @GeneratedValue
    private long id;
    private long value;

    @ManyToOne(fetch = FetchType.LAZY)
    private DBWheelGame dbWheelGame;

//    @OneToOne(cascade = CascadeType.ALL)
//    private DBUser dbUser;

    @OneToMany(mappedBy = "dbWheelGameEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DBProduct> products = new ArrayList<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DBUser dbUser;

    public DBUser getDbUser() {
        return dbUser;
    }

    public void setDbUser(DBUser dbUser) {
        this.dbUser = dbUser;
    }
}
