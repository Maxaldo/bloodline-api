package com.sang.demo.dtos;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiqueHopitalDto {

    private String nomHopital;
    private int totalPoches;
    private Map<String, Integer> pochesParStatut;
    private Map<String, Integer> pochesParGroupeSanguin;
}
