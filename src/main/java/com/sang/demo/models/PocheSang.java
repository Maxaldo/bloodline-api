package com.sang.demo.models;

import java.time.LocalDate;

import com.sang.demo.enums.StatutPoche;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "poche_sang")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PocheSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String numeroPoche;

    private LocalDate dateCollecte;

    private LocalDate dateExpiration;

    @Enumerated(EnumType.STRING)
    private StatutPoche statut;

    @ManyToOne
    @JoinColumn(name = "groupe_sanguin_id")
    private GroupeSanguin groupeSanguin;

    @ManyToOne
    @JoinColumn(name = "hopital_id")
    private Hopital hopital;
}
