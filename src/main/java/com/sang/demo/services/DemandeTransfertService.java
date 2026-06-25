package com.sang.demo.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sang.demo.dtos.DemandeTransfertRequestDto;
import com.sang.demo.dtos.DemandeTransfertResponseDto;
import com.sang.demo.dtos.ReponseTransfertDto;
import com.sang.demo.enums.StatutPoche;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.DemandeTransfert;
import com.sang.demo.models.GroupeSanguin;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.PocheSang;
import com.sang.demo.models.User;
import com.sang.demo.repositories.DemandeTransfertRepository;
import com.sang.demo.repositories.GroupeSanguinRepository;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.PocheSangRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DemandeTransfertService {

    private static final String EN_ATTENTE = "EN_ATTENTE";
    private static final String ACCEPTE = "ACCEPTE";
    private static final String REFUSE = "REFUSE";
    private static final String ANNULE = "ANNULE";

    private final DemandeTransfertRepository demandeTransfertRepository;
    private final HopitalRepository hopitalRepository;
    private final GroupeSanguinRepository groupeSanguinRepository;
    private final PocheSangRepository pocheSangRepository;
    private final MouvementStockService mouvementStockService;

    public DemandeTransfertResponseDto creerDemande(DemandeTransfertRequestDto dto, User demandeur) {
        Hopital hopitalFournisseur = hopitalRepository.findById(dto.getHopitalFournisseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + dto.getHopitalFournisseurId()));

        GroupeSanguin groupe = groupeSanguinRepository.findById(dto.getGroupeSanguinId())
                .orElseThrow(() -> new ResourceNotFoundException("Groupe sanguin non trouve avec l'id : " + dto.getGroupeSanguinId()));

        Hopital hopitalDemandeur = demandeur.getHopital();
        if (hopitalDemandeur == null) {
            throw new ResourceNotFoundException("Le demandeur n'est associe a aucun hopital");
        }

        if (hopitalDemandeur.getId() != null && hopitalDemandeur.getId().equals(hopitalFournisseur.getId())) {
            throw new IllegalArgumentException("L'hopital demandeur et l'hopital fournisseur doivent etre differents");
        }

        DemandeTransfert demande = DemandeTransfert.builder()
                .hopitalDemandeur(hopitalDemandeur)
                .hopitalFournisseur(hopitalFournisseur)
                .groupeSanguin(groupe)
                .quantiteDemandee(dto.getQuantiteDemandee())
                .statut(EN_ATTENTE)
                .motif(dto.getMotif())
                .demandeur(demandeur)
                .build();

        DemandeTransfert saved = demandeTransfertRepository.save(demande);
        log.info("Demande transfert creee id={} demandeurHopital={} fournisseurHopital={} groupe={} qte={}",
                saved.getId(),
                hopitalDemandeur.getId(),
                hopitalFournisseur.getId(),
                groupe.getId(),
                saved.getQuantiteDemandee());
        return toResponse(saved);
    }

    public DemandeTransfertResponseDto repondreDemande(Long demandeId, ReponseTransfertDto dto, User repondeur) {
        DemandeTransfert demande = demandeTransfertRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de transfert non trouvee avec l'id : " + demandeId));

        if (!EN_ATTENTE.equalsIgnoreCase(demande.getStatut())) {
            throw new IllegalArgumentException("La demande n'est pas en attente");
        }

        Hopital hopitalRepondeur = repondeur.getHopital();
        if (hopitalRepondeur == null || hopitalRepondeur.getId() == null
                || demande.getHopitalFournisseur() == null
                || !hopitalRepondeur.getId().equals(demande.getHopitalFournisseur().getId())) {
            throw new IllegalArgumentException("Vous n'etes pas autorise a repondre a cette demande");
        }

        String statut = dto.getStatut() == null ? "" : dto.getStatut().trim().toUpperCase();
        if (ACCEPTE.equals(statut)) {
            int quantite = demande.getQuantiteDemandee();
            List<PocheSang> poches = pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(
                    demande.getHopitalFournisseur().getId(),
                    demande.getGroupeSanguin().getId(),
                    StatutPoche.DISPONIBLE,
                    PageRequest.of(0, quantite)
            );

            if (poches.size() < quantite) {
                demande.setStatut(REFUSE);
                demande.setMotifRefus("Stock insuffisant");
                demande.setDateReponse(LocalDateTime.now());
                DemandeTransfert saved = demandeTransfertRepository.save(demande);
                log.info("Demande transfert refusee (stock insuffisant) id={}", saved.getId());
                return toResponse(saved);
            }

            Hopital hopitalDemandeur = demande.getHopitalDemandeur();
            for (PocheSang poche : poches) {
                poche.setHopital(hopitalDemandeur);
            }
            pocheSangRepository.saveAll(poches);

            demande.setStatut(ACCEPTE);
            demande.setDateReponse(LocalDateTime.now());
            DemandeTransfert saved = demandeTransfertRepository.save(demande);
            log.info("Demande transfert acceptee id={} pochesTransferees={}", saved.getId(), poches.size());

            PocheSang reference = poches.isEmpty() ? null : poches.get(0);
            mouvementStockService.enregistrerTransfert(
                    reference,
                    saved.getHopitalFournisseur(),
                    saved.getHopitalDemandeur(),
                    repondeur,
                    saved.getQuantiteDemandee()
            );
            return toResponse(saved);
        }

        if (REFUSE.equals(statut)) {
            if (dto.getMotifRefus() == null || dto.getMotifRefus().isBlank()) {
                throw new IllegalArgumentException("Le motifRefus est obligatoire si REFUSE");
            }
            demande.setStatut(REFUSE);
            demande.setMotifRefus(dto.getMotifRefus());
            demande.setDateReponse(LocalDateTime.now());
            DemandeTransfert saved = demandeTransfertRepository.save(demande);
            log.info("Demande transfert refusee id={} motif={}", saved.getId(), dto.getMotifRefus());
            return toResponse(saved);
        }

        throw new IllegalArgumentException("Statut invalide, utilisez ACCEPTE ou REFUSE");
    }

    public void annulerDemande(Long demandeId, User demandeur) {
        DemandeTransfert demande = demandeTransfertRepository.findById(demandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de transfert non trouvee avec l'id : " + demandeId));

        if (!EN_ATTENTE.equalsIgnoreCase(demande.getStatut())) {
            throw new IllegalArgumentException("La demande n'est pas en attente");
        }

        if (demande.getDemandeur() == null || demande.getDemandeur().getId() == null
                || demandeur == null || demandeur.getId() == null
                || !demande.getDemandeur().getId().equals(demandeur.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez annuler que vos propres demandes");
        }

        demande.setStatut(ANNULE);
        demande.setDateReponse(LocalDateTime.now());
        demandeTransfertRepository.save(demande);
        log.info("Demande transfert annulee id={}", demande.getId());
    }

    public List<DemandeTransfertResponseDto> getDemandesEnvoyees(Long hopitalId) {
        return demandeTransfertRepository.findByHopitalDemandeur_Id(hopitalId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DemandeTransfertResponseDto> getDemandesRecues(Long hopitalId) {
        return demandeTransfertRepository.findByHopitalFournisseur_Id(hopitalId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DemandeTransfertResponseDto> getDemandesEnAttente(Long hopitalId) {
        return demandeTransfertRepository.findByHopitalFournisseur_IdAndStatut(hopitalId, EN_ATTENTE).stream()
                .map(this::toResponse)
                .toList();
    }

    private DemandeTransfertResponseDto toResponse(DemandeTransfert demande) {
        String demandeurNom = null;
        if (demande.getDemandeur() != null) {
            String nom = demande.getDemandeur().getNom();
            String prenom = demande.getDemandeur().getPrenom();
            demandeurNom = ((nom == null ? "" : nom) + " " + (prenom == null ? "" : prenom)).trim();
        }

        return DemandeTransfertResponseDto.builder()
                .id(demande.getId())
                .hopitalDemandeur(demande.getHopitalDemandeur() != null ? demande.getHopitalDemandeur().getNom() : null)
                .hopitalFournisseur(demande.getHopitalFournisseur() != null ? demande.getHopitalFournisseur().getNom() : null)
                .groupeSanguin(demande.getGroupeSanguin() != null ? demande.getGroupeSanguin().getLibelle() : null)
                .quantiteDemandee(demande.getQuantiteDemandee())
                .statut(demande.getStatut())
                .motif(demande.getMotif())
                .motifRefus(demande.getMotifRefus())
                .demandeur(demandeurNom)
                .dateCreation(demande.getDateCreation())
                .dateReponse(demande.getDateReponse())
                .build();
    }
}

