package com.campus.device.util;

import com.campus.device.model.dto.PageResult;

import java.util.List;

public class PageUtil {

    private PageUtil() {}

    public static <T> PageResult<T> of(List<T> list, long total, int current, int size) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        return result;
    }

    public static int getOffset(int current, int size) {
        return (Math.max(current, 1) - 1) * size;
    }
}
