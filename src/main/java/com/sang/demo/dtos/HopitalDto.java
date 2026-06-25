package com.sang.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HopitalDto {

    @NotBlank
    private String nom;

    private String adresse;

    private String ville;

    private String telephone;

    private String region;
}
