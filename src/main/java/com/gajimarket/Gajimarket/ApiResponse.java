package com.gajimarket.Gajimarket;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {

    private String code;
    private T data;

    public ApiResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(String.valueOf(HttpStatus.OK.value()), data);
    }

}