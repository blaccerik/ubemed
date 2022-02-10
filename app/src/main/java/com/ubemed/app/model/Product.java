package com.ubemed.app.model;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreCats;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.LifecycleState;

import java.util.ArrayList;
import java.util.List;

import static com.ubemed.app.service.StoreService.decompressBytes;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long cost;
    private String seller;
    private String title;
    private List<String> cats;
    private byte[] file;
    private long id;

    public Product(DBProduct dbProduct) {
        this.cost = dbProduct.getCost();
        this.seller = dbProduct.getDbUser().getName();
        List<String> list = new ArrayList<>();
        for (DBStoreCats dbStoreCats : dbProduct.getDbStoreCats()) {
            list.add(dbStoreCats.getName());
        }
        this.cats = list;
//        this.cats = new String[1];
        this.title = dbProduct.getTitle();
        this.file = decompressBytes(dbProduct.getDbStoreImage().getFile());
        this.id = dbProduct.getId();
    }
}
