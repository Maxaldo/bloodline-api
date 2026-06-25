package com.sang.demo.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.RapportDto;
import com.sang.demo.dtos.StatistiqueRapportDto;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.Rapport;
import com.sang.demo.models.User;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.RapportRepository;
import com.sang.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RapportService {

    private final RapportRepository rapportRepository;
    private final HopitalRepository hopitalRepository;
    private final UserRepository userRepository;

    public List<Rapport> getAllRapports() {
        return rapportRepository.findAll();
    }

    public Rapport getRapportById(Long id) {
        return rapportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rapport non trouve avec l'id : " + id));
    }

    public Rapport createRapport(RapportDto dto, Long utilisateurId) {
        Hopital hopital = hopitalRepository.findById(dto.getHopitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + dto.getHopitalId()));

        User utilisateur = userRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec l'id : " + utilisateurId));

        Rapport rapport = Rapport.builder()
                .titre(dto.getTitre())
                .typeRapport(dto.getTypeRapport())
                .periodeDebut(dto.getPeriodeDebut())
                .periodeFin(dto.getPeriodeFin())
                .contenu(dto.getContenu())
                .hopital(hopital)
                .utilisateur(utilisateur)
                .build();

        return rapportRepository.save(rapport);
    }

    public void deleteRapport(Long id) {
        Rapport rapport = getRapportById(id);
        rapportRepository.delete(rapport);
    }

    public List<Rapport> getRapportsByHopital(Long hopitalId) {
        return rapportRepository.findByHopitalId(hopitalId);
    }

    public List<Rapport> getRapportsByUtilisateur(Long utilisateurId) {
        return rapportRepository.findByUtilisateurId(utilisateurId);
    }

    public StatistiqueRapportDto getStatistiquesRapports() {
        List<Rapport> tous = rapportRepository.findAll();

        Map<String, Integer> parHopital = new HashMap<>();
        for (Rapport r : tous) {
            String nom = r.getHopital() != null ? r.getHopital().getNom() : "Inconnu";
            parHopital.merge(nom, 1, Integer::sum);
        }

        Map<String, Integer> parType = new HashMap<>();
        for (Rapport r : tous) {
            String type = r.getTypeRapport() != null ? r.getTypeRapport() : "NON_DEFINI";
            parType.merge(type, 1, Integer::sum);
        }

        return StatistiqueRapportDto.builder()
                .totalRapports(tous.size())
                .rapportsParHopital(parHopital)
                .rapportsParType(parType)
                .build();
    }
}
