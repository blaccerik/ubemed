package com.ubemed.app.dtomodel;

import com.ubemed.app.dbmodel.DBProduct;
import com.ubemed.app.dbmodel.DBUser;
import com.ubemed.app.model.CasinoWheelPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelWinner {

    private long value;
    private long coins;
    private List<DBProduct> list;
    private DBUser dbUser;
    private List<CasinoWheelPlayer> players;
}
