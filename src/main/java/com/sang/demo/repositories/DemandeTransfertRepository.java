package com.sang.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.models.DemandeTransfert;

public interface DemandeTransfertRepository extends JpaRepository<DemandeTransfert, Long> {
    List<DemandeTransfert> findByHopitalDemandeur_Id(Long hopitalId);
    List<DemandeTransfert> findByHopitalFournisseur_Id(Long hopitalId);
    List<DemandeTransfert> findByHopitalFournisseur_IdAndStatut(Long hopitalId, String statut);
    List<DemandeTransfert> findByHopitalDemandeur_IdAndStatut(Long hopitalId, String statut);
}

