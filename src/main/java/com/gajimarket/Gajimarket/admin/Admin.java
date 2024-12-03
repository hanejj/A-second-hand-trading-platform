package com.gajimarket.Gajimarket.admin;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Admin {
    private int admin_idx;
    private String id;
    private String passwd;
    private String name;

    public Admin(int admin_idx, String id, String passwd, String name){
        this.admin_idx=admin_idx;
        this.id=id;
        this.passwd=passwd;
        this.name=name;
    }
}
