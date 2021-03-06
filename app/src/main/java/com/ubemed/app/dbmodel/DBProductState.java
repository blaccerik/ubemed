package com.ubemed.app.dbmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DBProductState {

    public enum states {
        sale,
        inventory,
        casino
    }

    @Id
    @GeneratedValue
    private long id;

    private states state;
    private String name;

    public DBProductState(states state, String name) {
        this.state = state;
        this.name = name;
    }
}
