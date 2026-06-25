package com.sang.demo.dtos;

import java.time.LocalDateTime;

import com.sang.demo.enums.RoleName;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfilResponseDto {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDateTime dateCreation;
    private RoleName role;
    private String hopital;
    private Long hopitalId;
}
