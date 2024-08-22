package com.example.managerproduct.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApiResponse<T> {
    private Integer code = 1000; // code = 1000 la thanh cong
    private String message;
    private T result;
}