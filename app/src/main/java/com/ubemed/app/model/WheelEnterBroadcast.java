package com.ubemed.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WheelEnterBroadcast {
    private String name;
    private long coins;
    private long value;
}
