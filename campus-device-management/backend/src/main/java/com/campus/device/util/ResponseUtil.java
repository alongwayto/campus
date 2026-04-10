package com.campus.device.util;

import com.campus.device.exception.Result;

public class ResponseUtil {

    private ResponseUtil() {}

    public static <T> Result<T> success(T data) {
        return Result.success(data);
    }

    public static Result<Void> success() {
        return Result.success(null);
    }

    public static <T> Result<T> fail(String message) {
        return Result.error(message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return Result.error(code, message);
    }
}
