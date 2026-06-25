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
public class StatistiqueRapportDto {

    private int totalRapports;
    private Map<String, Integer> rapportsParHopital;
    private Map<String, Integer> rapportsParType;
}
