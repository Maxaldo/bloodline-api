package com.sang.demo.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sang.demo.dtos.AccountResponseDto;
import com.sang.demo.dtos.LoginRequestDto;
import com.sang.demo.dtos.RegisterRequestDto;
import com.sang.demo.enums.RoleName;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.User;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.UserRepository;
import com.sang.demo.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final HopitalRepository hopitalRepository;

    public ResponseEntity<AccountResponseDto> register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(AccountResponseDto.builder()
                            .message("un utilisateur existe deja avec cet adresse email")
                            .build());
        }

        var user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(RoleName.PERSONNEL)
                .build();

        if (request.getHopitalId() != null) {
            hopitalRepository.findById(request.getHopitalId())
                    .ifPresent(user::setHopital);
        }

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(AccountResponseDto.builder()
                .id(savedUser.getId())
                .message("utilisateur enregistre avec succes")
                .build());
    }

    public ResponseEntity<AccountResponseDto> login(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("utilisateur introuvable"));

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(AccountResponseDto.builder()
                    .message("connexion reussie")
                    .token(token)
                    .build());

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(AccountResponseDto.builder()
                            .message("email ou mot de passe incorrect")
                            .build());
        }
    }
}
