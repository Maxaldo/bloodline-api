package com.sang.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.PocheSangDto;
import com.sang.demo.dtos.RechercheSanguinDto;
import com.sang.demo.dtos.StockResumeDto;
import com.sang.demo.enums.StatutPoche;
import com.sang.demo.models.PocheSang;
import com.sang.demo.models.User;
import com.sang.demo.services.PocheSangService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/poches")
@RequiredArgsConstructor
public class PocheSangController {

    private final PocheSangService pocheSangService;

    @GetMapping
    public ResponseEntity<List<PocheSang>> getAllPoches() {
        return ResponseEntity.ok(pocheSangService.getAllPoches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PocheSang> getPocheById(@PathVariable Long id) {
        return ResponseEntity.ok(pocheSangService.getPocheById(id));
    }

    @PostMapping
    public ResponseEntity<PocheSang> createPoche(@Valid @RequestBody PocheSangDto dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pocheSangService.createPoche(dto, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PocheSang> updatePoche(@PathVariable Long id,
                                                 @Valid @RequestBody PocheSangDto dto,
                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pocheSangService.updatePoche(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoche(@PathVariable Long id, @AuthenticationPrincipal User user) {
        pocheSangService.deletePoche(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hopital/{hopitalId}")
    public ResponseEntity<List<PocheSang>> getPochesByHopital(@PathVariable Long hopitalId) {
        return ResponseEntity.ok(pocheSangService.getPochesByHopital(hopitalId));
    }

    @GetMapping("/groupe-sanguin/{groupeSanguinId}")
    public ResponseEntity<List<PocheSang>> getPochesByGroupeSanguin(@PathVariable Long groupeSanguinId) {
        return ResponseEntity.ok(pocheSangService.getPochesByGroupeSanguin(groupeSanguinId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<PocheSang>> getPochesByStatut(@PathVariable StatutPoche statut) {
        return ResponseEntity.ok(pocheSangService.getPochesByStatut(statut));
    }

    @DeleteMapping("/perimees/hopital/{hopitalId}")
    public ResponseEntity<Map<String, Object>> deletePochesPerimees(@PathVariable Long hopitalId,
                                                                    @AuthenticationPrincipal User user) {
        int count = pocheSangService.deletePochesPerimees(hopitalId, user);
        return ResponseEntity.ok(Map.of(
                "message", "poches perimees supprimees",
                "nombreSupprime", count
        ));
    }

    @GetMapping("/expiration/hopital/{hopitalId}")
    public ResponseEntity<List<PocheSang>> getPochesProchesExpiration(
            @PathVariable Long hopitalId,
            @RequestParam(defaultValue = "7") int jours) {
        return ResponseEntity.ok(pocheSangService.getPochesProchesExpiration(hopitalId, jours));
    }

    @GetMapping("/stock/hopital/{hopitalId}")
    public ResponseEntity<List<StockResumeDto>> getStockResume(@PathVariable Long hopitalId) {
        return ResponseEntity.ok(pocheSangService.getStockResume(hopitalId));
    }

    @GetMapping("/stock/global")
    public ResponseEntity<List<StockResumeDto>> getStockResumeGlobal() {
        return ResponseEntity.ok(pocheSangService.getStockResumeGlobal());
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<RechercheSanguinDto>> rechercherPochesDisponibles(
            @RequestParam Long groupeSanguinId,
            @RequestParam Long excludeHopitalId) {
        return ResponseEntity.ok(pocheSangService.rechercherPochesDisponibles(groupeSanguinId, excludeHopitalId));
    }
}
