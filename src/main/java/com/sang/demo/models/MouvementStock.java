package com.sang.demo.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mouvement_stock")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeMouvement;

    @ManyToOne
    @JoinColumn(name = "poche_sang_id")
    private PocheSang pocheSang;

    private String numeroPoche;

    private String groupeSanguin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hopital_id", nullable = false)
    private Hopital hopital;

    private String hopitalSource;

    private String hopitalDestination;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    private String description;

    @Builder.Default
    private int quantite = 1;

    private LocalDateTime dateMouvement;

    @PrePersist
    protected void onCreate() {
        this.dateMouvement = LocalDateTime.now();
    }
}

