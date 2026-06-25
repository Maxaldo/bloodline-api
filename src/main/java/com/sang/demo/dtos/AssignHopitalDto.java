package com.sang.demo.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignHopitalDto {

    @NotNull
    private Long hopitalId;
}
