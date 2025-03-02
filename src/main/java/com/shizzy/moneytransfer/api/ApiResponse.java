package com.shizzy.moneytransfer.api;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse <T> implements Serializable {
    private boolean success;
    private String message;
    private T data;
}
