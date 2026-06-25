package com.sang.demo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemandeTransfertRequestDto {
    @NotNull
    private Long hopitalFournisseurId;

    @NotNull
    private Long groupeSanguinId;

    @Min(1)
    private int quantiteDemandee;

    private String motif;
}

