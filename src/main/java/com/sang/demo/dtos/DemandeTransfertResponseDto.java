package com.sang.demo.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DemandeTransfertResponseDto {
    private Long id;
    private String hopitalDemandeur;
    private String hopitalFournisseur;
    private String groupeSanguin;
    private int quantiteDemandee;
    private String statut;
    private String motif;
    private String motifRefus;
    private String demandeur;
    private LocalDateTime dateCreation;
    private LocalDateTime dateReponse;
}

