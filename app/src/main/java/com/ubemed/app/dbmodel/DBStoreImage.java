package com.ubemed.app.dbmodel;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class DBStoreImage {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    //image bytes can have large lengths so we specify a value
    //which is more than the default length for picByte column
    @Column(name = "file", length = 1000)
    private byte[] file;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private DBProduct dbProduct;

    public DBStoreImage() {
        super();
    }

    public DBStoreImage(String name, String type, byte[] file) {
        this.name = name;
        this.type = type;
        this.file = file;
    }
}
