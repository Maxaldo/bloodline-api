package com.sang.demo.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PocheSangDto {

    @NotBlank
    private String numeroPoche;

    private LocalDate dateCollecte;

    private LocalDate dateExpiration;

    @NotNull
    private String statut;

    @NotNull
    private Long groupeSanguinId;

    @NotNull
    private Long hopitalId;
}
