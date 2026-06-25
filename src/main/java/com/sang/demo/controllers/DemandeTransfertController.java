package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.DemandeTransfertRequestDto;
import com.sang.demo.dtos.DemandeTransfertResponseDto;
import com.sang.demo.dtos.ReponseTransfertDto;
import com.sang.demo.models.User;
import com.sang.demo.services.DemandeTransfertService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/transferts")
@RequiredArgsConstructor
@Slf4j
public class DemandeTransfertController {

    private final DemandeTransfertService demandeTransfertService;

    @PostMapping
    public ResponseEntity<DemandeTransfertResponseDto> creer(@Valid @RequestBody DemandeTransfertRequestDto dto,
                                                             @AuthenticationPrincipal User user) {
        DemandeTransfertResponseDto created = demandeTransfertService.creerDemande(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/repondre")
    public ResponseEntity<DemandeTransfertResponseDto> repondre(@PathVariable Long id,
                                                                @Valid @RequestBody ReponseTransfertDto dto,
                                                                @AuthenticationPrincipal User user) {
        DemandeTransfertResponseDto updated = demandeTransfertService.repondreDemande(id, dto, user);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Void> annuler(@PathVariable Long id, @AuthenticationPrincipal User user) {
        demandeTransfertService.annulerDemande(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/envoyees")
    public ResponseEntity<List<DemandeTransfertResponseDto>> envoyees(@AuthenticationPrincipal User user) {
        if (user.getHopital() == null || user.getHopital().getId() == null) {
            log.warn("Utilisateur sans hopital tente d'acceder aux demandes envoyees userId={}", user.getId());
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(demandeTransfertService.getDemandesEnvoyees(user.getHopital().getId()));
    }

    @GetMapping("/recues")
    public ResponseEntity<List<DemandeTransfertResponseDto>> recues(@AuthenticationPrincipal User user) {
        if (user.getHopital() == null || user.getHopital().getId() == null) {
            log.warn("Utilisateur sans hopital tente d'acceder aux demandes recues userId={}", user.getId());
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(demandeTransfertService.getDemandesRecues(user.getHopital().getId()));
    }

    @GetMapping("/en-attente")
    public ResponseEntity<List<DemandeTransfertResponseDto>> enAttente(@AuthenticationPrincipal User user) {
        if (user.getHopital() == null || user.getHopital().getId() == null) {
            log.warn("Utilisateur sans hopital tente d'acceder aux demandes en attente userId={}", user.getId());
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok(demandeTransfertService.getDemandesEnAttente(user.getHopital().getId()));
    }
}

