package com.ubemed.app.model;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBStoreCats;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long price;
    private long bid;
    private List<BidResponse> bids;
    private String seller;
    private String title;
    private List<Long> cats;
    private long id;

    public Product(DBProduct dbProduct) {
        this.bid = dbProduct.getHighestBid();
        this.price = dbProduct.getPrice();
        this.seller = dbProduct.getDbUser().getName();
        List<Long> list = new ArrayList<>();
        for (DBStoreCats dbStoreCats : dbProduct.getDbStoreCats()) {
            list.add(dbStoreCats.getId());
        }
        this.bids = dbProduct.getBids().stream().map(o -> new BidResponse(o)).collect(Collectors.toList());
        this.cats = list;
        this.title = dbProduct.getTitle();
        this.id = dbProduct.getId();
    }
}
