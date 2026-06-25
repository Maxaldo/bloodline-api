package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.dtos.AssignHopitalDto;
import com.sang.demo.dtos.ChangeRoleDto;
import com.sang.demo.dtos.ProfilResponseDto;
import com.sang.demo.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/utilisateurs")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProfilResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfilResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ProfilResponseDto> changeRole(@PathVariable Long id,
                                                         @Valid @RequestBody ChangeRoleDto dto) {
        return ResponseEntity.ok(userService.changeRole(id, dto.getRole()));
    }

    @PutMapping("/{id}/hopital")
    public ResponseEntity<ProfilResponseDto> assignHopital(@PathVariable Long id,
                                                            @Valid @RequestBody AssignHopitalDto dto) {
        return ResponseEntity.ok(userService.assignHopital(id, dto.getHopitalId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
