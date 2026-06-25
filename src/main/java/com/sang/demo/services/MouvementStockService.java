package com.sang.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.MouvementStockResponseDto;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.MouvementStock;
import com.sang.demo.models.PocheSang;
import com.sang.demo.models.User;
import com.sang.demo.repositories.MouvementStockRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;

    public void enregistrerMouvement(String type, PocheSang poche, Hopital hopital, User utilisateur, String description) {
        if (hopital == null || hopital.getId() == null) {
            log.warn("Impossible d'enregistrer mouvement: hopital null");
            return;
        }
        if (utilisateur == null || utilisateur.getId() == null) {
            log.warn("Impossible d'enregistrer mouvement: utilisateur null");
            return;
        }

        String numeroPoche = poche != null ? poche.getNumeroPoche() : null;
        String groupeSanguin = (poche != null && poche.getGroupeSanguin() != null) ? poche.getGroupeSanguin().getLibelle() : null;

        MouvementStock mouvement = MouvementStock.builder()
                .typeMouvement(type)
                .pocheSang(poche)
                .numeroPoche(numeroPoche)
                .groupeSanguin(groupeSanguin)
                .hopital(hopital)
                .utilisateur(utilisateur)
                .description(description)
                .build();

        mouvementStockRepository.save(mouvement);
    }

    public void enregistrerTransfert(PocheSang poche, Hopital source, Hopital destination, User utilisateur, int quantite) {
        if (source == null || destination == null) {
            log.warn("Transfert: source ou destination null");
            return;
        }
        String numeroPoche = poche != null ? poche.getNumeroPoche() : null;
        String groupeSanguin = (poche != null && poche.getGroupeSanguin() != null) ? poche.getGroupeSanguin().getLibelle() : null;

        MouvementStock sortant = MouvementStock.builder()
                .typeMouvement("TRANSFERT_SORTANT")
                .pocheSang(poche)
                .numeroPoche(numeroPoche)
                .groupeSanguin(groupeSanguin)
                .hopital(source)
                .hopitalSource(source.getNom())
                .hopitalDestination(destination.getNom())
                .utilisateur(utilisateur)
                .quantite(quantite)
                .description("Transfert sortant vers " + destination.getNom())
                .build();

        MouvementStock entrant = MouvementStock.builder()
                .typeMouvement("TRANSFERT_ENTRANT")
                .pocheSang(poche)
                .numeroPoche(numeroPoche)
                .groupeSanguin(groupeSanguin)
                .hopital(destination)
                .hopitalSource(source.getNom())
                .hopitalDestination(destination.getNom())
                .utilisateur(utilisateur)
                .quantite(quantite)
                .description("Transfert entrant depuis " + source.getNom())
                .build();

        mouvementStockRepository.saveAll(List.of(sortant, entrant));
    }

    public List<MouvementStockResponseDto> getHistoriqueParHopital(Long hopitalId) {
        return mouvementStockRepository.findByHopital_IdOrderByDateMouvementDesc(hopitalId).stream()
                .map(this::toDto)
                .toList();
    }

    public List<MouvementStockResponseDto> getHistoriqueParHopitalEtType(Long hopitalId, String type) {
        return mouvementStockRepository.findByHopital_IdAndTypeMouvementOrderByDateMouvementDesc(hopitalId, type).stream()
                .map(this::toDto)
                .toList();
    }

    public List<MouvementStockResponseDto> getHistoriqueGlobal() {
        return mouvementStockRepository.findAllByOrderByDateMouvementDesc().stream()
                .map(this::toDto)
                .toList();
    }

    private MouvementStockResponseDto toDto(MouvementStock mouvement) {
        String utilisateurNom = null;
        if (mouvement.getUtilisateur() != null) {
            String nom = mouvement.getUtilisateur().getNom();
            String prenom = mouvement.getUtilisateur().getPrenom();
            utilisateurNom = ((nom == null ? "" : nom) + " " + (prenom == null ? "" : prenom)).trim();
        }

        return MouvementStockResponseDto.builder()
                .id(mouvement.getId())
                .typeMouvement(mouvement.getTypeMouvement())
                .numeroPoche(mouvement.getNumeroPoche())
                .groupeSanguin(mouvement.getGroupeSanguin())
                .nomHopital(mouvement.getHopital() != null ? mouvement.getHopital().getNom() : null)
                .hopitalSource(mouvement.getHopitalSource())
                .hopitalDestination(mouvement.getHopitalDestination())
                .utilisateur(utilisateurNom)
                .description(mouvement.getDescription())
                .quantite(mouvement.getQuantite())
                .dateMouvement(mouvement.getDateMouvement())
                .build();
    }
}

