package com.sang.demo.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RechercheSanguinDto {
    private String nomHopital;
    private Long hopitalId;
    private String ville;
    private String telephone;
    private String groupeSanguin;
    private int quantiteDisponible;
}

