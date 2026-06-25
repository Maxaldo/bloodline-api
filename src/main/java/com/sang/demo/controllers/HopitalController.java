package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.HopitalDto;
import com.sang.demo.models.Hopital;
import com.sang.demo.services.HopitalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hopitaux")
@RequiredArgsConstructor
public class HopitalController {

    private final HopitalService hopitalService;

    @GetMapping
    public ResponseEntity<List<Hopital>> getAllHopitaux() {
        return ResponseEntity.ok(hopitalService.getAllHopitaux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hopital> getHopitalById(@PathVariable Long id) {
        return ResponseEntity.ok(hopitalService.getHopitalById(id));
    }

    @PostMapping
    public ResponseEntity<Hopital> createHopital(@Valid @RequestBody HopitalDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hopitalService.createHopital(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hopital> updateHopital(@PathVariable Long id, @Valid @RequestBody HopitalDto dto) {
        return ResponseEntity.ok(hopitalService.updateHopital(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHopital(@PathVariable Long id) {
        hopitalService.deleteHopital(id);
        return ResponseEntity.noContent().build();
    }
}
