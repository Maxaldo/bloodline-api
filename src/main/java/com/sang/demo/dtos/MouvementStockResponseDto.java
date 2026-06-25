package com.sang.demo.dtos;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MouvementStockResponseDto {
    private Long id;
    private String typeMouvement;
    private String numeroPoche;
    private String groupeSanguin;
    private String nomHopital;
    private String hopitalSource;
    private String hopitalDestination;
    private String utilisateur;
    private String description;
    private int quantite;
    private LocalDateTime dateMouvement;
}

