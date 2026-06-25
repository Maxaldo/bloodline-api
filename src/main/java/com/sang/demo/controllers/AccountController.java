package com.sang.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.AccountResponseDto;
import com.sang.demo.dtos.LoginRequestDto;
import com.sang.demo.dtos.ProfilDto;
import com.sang.demo.dtos.ProfilResponseDto;
import com.sang.demo.dtos.RegisterRequestDto;
import com.sang.demo.models.User;
import com.sang.demo.repositories.UserRepository;
import com.sang.demo.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AccountController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AccountResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return authenticationService.login(request);
    }

    @GetMapping("/profil")
    public ResponseEntity<ProfilResponseDto> getProfil(@AuthenticationPrincipal User user) {
        ProfilResponseDto profil = ProfilResponseDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .dateCreation(user.getDateCreation())
                .role(user.getRole())
                .hopital(user.getHopital() != null ? user.getHopital().getNom() : null)
                .hopitalId(user.getHopital() != null ? user.getHopital().getId() : null)
                .build();
        return ResponseEntity.ok(profil);
    }

    @PutMapping("/profil")
    public ResponseEntity<ProfilResponseDto> updateProfil(@AuthenticationPrincipal User user,
                                                           @Valid @RequestBody ProfilDto dto) {
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());
        User updated = userRepository.save(user);

        ProfilResponseDto profil = ProfilResponseDto.builder()
                .id(updated.getId())
                .nom(updated.getNom())
                .prenom(updated.getPrenom())
                .email(updated.getEmail())
                .telephone(updated.getTelephone())
                .dateCreation(updated.getDateCreation())
                .role(updated.getRole())
                .hopital(updated.getHopital() != null ? updated.getHopital().getNom() : null)
                .hopitalId(updated.getHopital() != null ? updated.getHopital().getId() : null)
                .build();
        return ResponseEntity.ok(profil);
    }

    @DeleteMapping("/profil")
    public ResponseEntity<Void> deleteProfil(@AuthenticationPrincipal User user) {
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
