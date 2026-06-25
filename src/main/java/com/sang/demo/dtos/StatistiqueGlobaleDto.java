package com.sang.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueGlobaleDto {

    private int totalPoches;
    private int pochesDisponibles;
    private int pochesReservees;
    private int pochesUtilisees;
    private int pochesExpirees;
    private int totalHopitaux;
    private int totalUtilisateurs;
}
