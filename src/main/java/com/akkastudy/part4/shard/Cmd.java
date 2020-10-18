package com.akkastudy.part4.shard;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cmd implements Serializable {
    public final static String BUY = "buy";
    public final static String QUERY = "query";
    public final static String DEL = "del";

    private String action;
    private int userId;
    private Item item;

    public Cmd(String action, int userId) {
        this.action = action;
        this.userId = userId;
    }
}
