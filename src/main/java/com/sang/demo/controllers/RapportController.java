package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.RapportDto;
import com.sang.demo.dtos.StatistiqueRapportDto;
import com.sang.demo.models.Rapport;
import com.sang.demo.models.User;
import com.sang.demo.services.RapportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rapports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @GetMapping
    public ResponseEntity<List<Rapport>> getAllRapports() {
        return ResponseEntity.ok(rapportService.getAllRapports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rapport> getRapportById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.getRapportById(id));
    }

    @PostMapping
    public ResponseEntity<Rapport> createRapport(@Valid @RequestBody RapportDto dto,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rapportService.createRapport(dto, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hopital/{hopitalId}")
    public ResponseEntity<List<Rapport>> getRapportsByHopital(@PathVariable Long hopitalId) {
        return ResponseEntity.ok(rapportService.getRapportsByHopital(hopitalId));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Rapport>> getRapportsByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(rapportService.getRapportsByUtilisateur(utilisateurId));
    }

    @GetMapping("/statistiques")
    public ResponseEntity<StatistiqueRapportDto> getStatistiquesRapports() {
        return ResponseEntity.ok(rapportService.getStatistiquesRapports());
    }
}
