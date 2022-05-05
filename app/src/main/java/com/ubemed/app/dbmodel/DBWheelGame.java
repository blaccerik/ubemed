package com.ubemed.app.dbmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBWheelGame {

    @Id
    @GeneratedValue
    private long id;
    private long value;
    private Date createTime;

    @OneToMany(mappedBy = "dbWheelGame", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DBWheelGameEntry> dbWheelGameEntries = new ArrayList<>();

    public void addEntry(DBWheelGameEntry dbWheelGameEntry) {
        dbWheelGameEntries.add(dbWheelGameEntry);
        dbWheelGameEntry.setDbWheelGame(this);
    }
}
