package com.ubemed.app.dbmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBStoreCats {

    @Id
    @GeneratedValue
    private long id;
    @Column(name = "code")
    private long code;
    @Column(name = "name")
    private String name;

    public DBStoreCats(long code, String name) {
        this.code = code;
        this.name = name;
    }
}
