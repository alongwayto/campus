package com.campus.device.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageResult<T> {

    private long total;
    private List<T> list;
    private int current;
    private int size;
    private int pages;

    public PageResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
}
