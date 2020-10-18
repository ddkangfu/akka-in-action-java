package com.akkastudy.part4.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Article implements Serializable {
    private String id;
    private String content;
}
