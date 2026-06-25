package com.sang.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResumeDto {

    private String groupeSanguin;
    private int quantiteDisponible;
    private int quantiteReservee;
    private int quantiteExpiree;
}
