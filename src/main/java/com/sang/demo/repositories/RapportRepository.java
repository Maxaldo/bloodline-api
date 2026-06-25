package com.sang.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.models.Rapport;

public interface RapportRepository extends JpaRepository<Rapport, Long> {

    List<Rapport> findByUtilisateurId(Long utilisateurId);

    List<Rapport> findByHopitalId(Long hopitalId);
}
