package com.sang.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReponseTransfertDto {
    @NotBlank
    private String statut; // "ACCEPTE" ou "REFUSE"

    private String motifRefus; // obligatoire si REFUSE
}

