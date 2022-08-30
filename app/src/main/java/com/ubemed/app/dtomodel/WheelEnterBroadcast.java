package com.ubemed.app.dtomodel;

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
public class WheelEnterBroadcast {
    private boolean isNormalBroadcast;
    private long mid;
    private DTOUser user;
    private long value;
    private List<CasinoWheelPlayer> players;
}
