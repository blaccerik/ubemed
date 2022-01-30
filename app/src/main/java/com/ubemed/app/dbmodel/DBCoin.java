package com.ubemed.app.dbmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class DBCoin {

    @Id
    @GeneratedValue
    private long id;
    private long coins;

    @OneToOne(fetch = FetchType.LAZY)
    private DBUser dbUser;
}
