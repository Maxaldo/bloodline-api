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
@Table(name = "demande_transfert")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandeTransfert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hopital_demandeur_id", nullable = false)
    private Hopital hopitalDemandeur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hopital_fournisseur_id", nullable = false)
    private Hopital hopitalFournisseur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "groupe_sanguin_id", nullable = false)
    private GroupeSanguin groupeSanguin;

    private int quantiteDemandee;

    private String statut;

    private String motif;

    private String motifRefus;

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private User demandeur;

    private LocalDateTime dateCreation;

    private LocalDateTime dateReponse;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}

