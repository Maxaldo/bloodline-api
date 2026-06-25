package com.sang.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sang.demo.dtos.AccountResponseDto;
import com.sang.demo.dtos.LoginRequestDto;
import com.sang.demo.dtos.RegisterRequestDto;
import com.sang.demo.enums.RoleName;
import com.sang.demo.models.User;
import com.sang.demo.repositories.UserRepository;
import com.sang.demo.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_WithValidData_ShouldReturnSuccessMessage() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@gmail.com");
        request.setMotDePasse("password123");
        request.setTelephone("0612345678");

        when(userRepository.findByEmail("jean@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(User.builder().id(1L).build());

        ResponseEntity<AccountResponseDto> response = authenticationService.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("succes"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnBadRequest() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("existe@gmail.com");

        User existingUser = User.builder().id(1L).email("existe@gmail.com").build();
        when(userRepository.findByEmail("existe@gmail.com")).thenReturn(Optional.of(existingUser));

        ResponseEntity<AccountResponseDto> response = authenticationService.register(request);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("existe deja"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("jean@gmail.com");
        request.setMotDePasse("password123");

        User user = User.builder()
                .id(1L)
                .email("jean@gmail.com")
                .motDePasse("encoded")
                .role(RoleName.PERSONNEL)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("jean@gmail.com", "password123"));
        when(userRepository.findByEmail("jean@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake.jwt.token");

        ResponseEntity<AccountResponseDto> response = authenticationService.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().getToken());
        assertEquals("fake.jwt.token", response.getBody().getToken());
        assertTrue(response.getBody().getMessage().contains("reussie"));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturn401() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("jean@gmail.com");
        request.setMotDePasse("mauvais");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<AccountResponseDto> response = authenticationService.login(request);

        assertEquals(401, response.getStatusCode().value());
        assertTrue(response.getBody().getMessage().contains("incorrect"));
        assertNull(response.getBody().getToken());
    }
}
