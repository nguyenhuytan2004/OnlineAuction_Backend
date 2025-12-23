package com.example.backend.model.Momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomoCreateResponse {
    private Integer resultCode;
    private String message;
    private String payUrl;
}