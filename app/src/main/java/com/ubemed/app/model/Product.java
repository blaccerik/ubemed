package com.ubemed.app.model;

import com.ubemed.app.dbmodel.DBBid;
import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreCats;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long cost;
    private String seller;
    private String title;
    private List<String> cats;
    private long id;

//    private long myBid;

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
//        this.file = decompressBytes(dbProduct.getDbStoreImage().getFile());
        this.id = dbProduct.getId();
//        this.myBid = 0;
    }

//    public Product(DBProduct dbProduct, DBBid dbBid) {
//        Product product = new Product(dbProduct);
//        product.setMyBid(dbBid.getAmount());
//        this.cost = dbProduct.getCost();
//        this.seller = dbProduct.getDbUser().getName();
//        List<String> list = new ArrayList<>();
//        for (DBStoreCats dbStoreCats : dbProduct.getDbStoreCats()) {
//            list.add(dbStoreCats.getName());
//        }
//        this.cats = list;
////        this.cats = new String[1];
//        this.title = dbProduct.getTitle();
////        this.file = decompressBytes(dbProduct.getDbStoreImage().getFile());
//        this.id = dbProduct.getId();
//    }
}
