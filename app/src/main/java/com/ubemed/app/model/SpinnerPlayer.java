package com.ubemed.app.model;

import com.ubemed.app.dtomodel.DTOUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpinnerPlayer {
    private DTOUser dtoUser;
    private long value;
}