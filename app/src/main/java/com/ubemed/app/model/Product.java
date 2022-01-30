package com.ubemed.app.model;

import com.ubemed.app.dbmodel.DBProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.ubemed.app.service.StoreService.decompressBytes;

@Getter
@Setter
@NoArgsConstructor
public class Product {

    private long cost;
    private String seller;
    private String title;
    private String description;
    private byte[] picByte;
    private long id;

    public Product(DBProduct dbProduct) {
        this.cost = dbProduct.getCost();
        this.seller = dbProduct.getDbUser().getName();
        this.description = dbProduct.getDescription();
        this.title = dbProduct.getTitle();
        this.picByte = decompressBytes(dbProduct.getDbStoreImage().getPicByte());
        this.id = dbProduct.getId();
    }
}
