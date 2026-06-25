package com.sang.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.StatistiqueGlobaleDto;
import com.sang.demo.dtos.StatistiqueHopitalDto;
import com.sang.demo.services.StatistiqueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/global")
    public ResponseEntity<StatistiqueGlobaleDto> getStatistiquesGlobales() {
        return ResponseEntity.ok(statistiqueService.getStatistiquesGlobales());
    }

    @GetMapping("/hopital/{hopitalId}")
    public ResponseEntity<StatistiqueHopitalDto> getStatistiquesHopital(@PathVariable Long hopitalId) {
        return ResponseEntity.ok(statistiqueService.getStatistiquesHopital(hopitalId));
    }
}
