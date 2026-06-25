package com.sang.demo.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {
    private Long id;
    private String message;
    private String token;
}
