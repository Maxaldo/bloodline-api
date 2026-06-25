package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.MouvementStockResponseDto;
import com.sang.demo.services.MouvementStockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/mouvements")
@RequiredArgsConstructor
@Slf4j
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @GetMapping("/hopital/{hopitalId}")
    public ResponseEntity<List<MouvementStockResponseDto>> getHistoriqueHopital(@PathVariable Long hopitalId,
                                                                               @RequestParam(required = false) String type) {
        if (type != null && !type.isBlank()) {
            return ResponseEntity.ok(mouvementStockService.getHistoriqueParHopitalEtType(hopitalId, type));
        }
        return ResponseEntity.ok(mouvementStockService.getHistoriqueParHopital(hopitalId));
    }

    @GetMapping("/global")
    public ResponseEntity<List<MouvementStockResponseDto>> getHistoriqueGlobal() {
        return ResponseEntity.ok(mouvementStockService.getHistoriqueGlobal());
    }
}

