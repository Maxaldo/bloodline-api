package com.sang.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.StatistiqueGlobaleDto;
import com.sang.demo.dtos.StatistiqueHopitalDto;
import com.sang.demo.enums.StatutPoche;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.PocheSang;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.PocheSangRepository;
import com.sang.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StatistiqueService {

    private final PocheSangRepository pocheSangRepository;
    private final HopitalRepository hopitalRepository;
    private final UserRepository userRepository;

    public StatistiqueGlobaleDto getStatistiquesGlobales() {
        List<PocheSang> toutes = pocheSangRepository.findAll();

        return StatistiqueGlobaleDto.builder()
                .totalPoches(toutes.size())
                .pochesDisponibles(countByStatut(toutes, StatutPoche.DISPONIBLE))
                .pochesReservees(countByStatut(toutes, StatutPoche.RESERVE))
                .pochesUtilisees(countByStatut(toutes, StatutPoche.UTILISE))
                .pochesExpirees(countByStatut(toutes, StatutPoche.EXPIRE))
                .totalHopitaux((int) hopitalRepository.count())
                .totalUtilisateurs((int) userRepository.count())
                .build();
    }

    public StatistiqueHopitalDto getStatistiquesHopital(Long hopitalId) {
        Hopital hopital = hopitalRepository.findById(hopitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + hopitalId));

        List<PocheSang> poches = pocheSangRepository.findByHopitalId(hopitalId);

        Map<String, Integer> parStatut = new HashMap<>();
        for (StatutPoche statut : StatutPoche.values()) {
            parStatut.put(statut.name(), countByStatut(poches, statut));
        }

        Map<String, Integer> parGroupe = new HashMap<>();
        for (PocheSang poche : poches) {
            String libelle = poche.getGroupeSanguin().getLibelle();
            parGroupe.merge(libelle, 1, Integer::sum);
        }

        return StatistiqueHopitalDto.builder()
                .nomHopital(hopital.getNom())
                .totalPoches(poches.size())
                .pochesParStatut(parStatut)
                .pochesParGroupeSanguin(parGroupe)
                .build();
    }

    private int countByStatut(List<PocheSang> poches, StatutPoche statut) {
        return (int) poches.stream().filter(p -> p.getStatut() == statut).count();
    }
}
