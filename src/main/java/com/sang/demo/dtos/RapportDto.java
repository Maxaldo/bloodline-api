package com.sang.demo.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RapportDto {

    @NotBlank
    private String titre;

    private String typeRapport;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    private String contenu;

    @NotNull
    private Long hopitalId;
}
