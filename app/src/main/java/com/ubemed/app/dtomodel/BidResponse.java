package com.ubemed.app.dtomodel;

import com.ubemed.app.dbmodel.DBBid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {

    String username;
    long id;
    long amount;

    public BidResponse(DBBid dbBid) {
        this.username = dbBid.getDbUser().getName();
        this.id = -1;
        this.amount = dbBid.getAmount();
    }
}
