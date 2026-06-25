package com.sang.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeRoleDto {

    @NotBlank
    private String role;
}
