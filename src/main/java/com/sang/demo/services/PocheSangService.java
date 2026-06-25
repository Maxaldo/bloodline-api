package com.sang.demo.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.PocheSangDto;
import com.sang.demo.dtos.RechercheSanguinDto;
import com.sang.demo.dtos.StockResumeDto;
import com.sang.demo.enums.StatutPoche;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.GroupeSanguin;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.PocheSang;
import com.sang.demo.models.User;
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
public class PocheSangService {

    private final PocheSangRepository pocheSangRepository;
    private final GroupeSanguinRepository groupeSanguinRepository;
    private final HopitalRepository hopitalRepository;
    private final MouvementStockService mouvementStockService;

    public List<PocheSang> getAllPoches() {
        return pocheSangRepository.findAll();
    }

    public PocheSang getPocheById(Long id) {
        return pocheSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poche de sang non trouvee avec l'id : " + id));
    }

    public PocheSang createPoche(PocheSangDto dto, User utilisateur) {
        GroupeSanguin groupe = groupeSanguinRepository.findById(dto.getGroupeSanguinId())
                .orElseThrow(() -> new ResourceNotFoundException("Groupe sanguin non trouve avec l'id : " + dto.getGroupeSanguinId()));

        Hopital hopital = hopitalRepository.findById(dto.getHopitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + dto.getHopitalId()));

        PocheSang poche = PocheSang.builder()
                .numeroPoche(dto.getNumeroPoche())
                .dateCollecte(dto.getDateCollecte())
                .dateExpiration(dto.getDateExpiration())
                .statut(StatutPoche.valueOf(dto.getStatut()))
                .groupeSanguin(groupe)
                .hopital(hopital)
                .build();

        PocheSang saved = pocheSangRepository.save(poche);
        mouvementStockService.enregistrerMouvement(
                "ENTREE",
                saved,
                hopital,
                utilisateur,
                "Ajout de la poche " + saved.getNumeroPoche() + " (" + groupe.getLibelle() + ")"
        );
        return saved;
    }

    public PocheSang updatePoche(Long id, PocheSangDto dto, User utilisateur) {
        PocheSang poche = getPocheById(id);

        GroupeSanguin groupe = groupeSanguinRepository.findById(dto.getGroupeSanguinId())
                .orElseThrow(() -> new ResourceNotFoundException("Groupe sanguin non trouve avec l'id : " + dto.getGroupeSanguinId()));

        Hopital hopital = hopitalRepository.findById(dto.getHopitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + dto.getHopitalId()));

        poche.setNumeroPoche(dto.getNumeroPoche());
        poche.setDateCollecte(dto.getDateCollecte());
        poche.setDateExpiration(dto.getDateExpiration());
        poche.setStatut(StatutPoche.valueOf(dto.getStatut()));
        poche.setGroupeSanguin(groupe);
        poche.setHopital(hopital);

        PocheSang saved = pocheSangRepository.save(poche);
        mouvementStockService.enregistrerMouvement(
                "MODIFICATION",
                saved,
                hopital,
                utilisateur,
                "Modification de la poche " + saved.getNumeroPoche()
        );
        return saved;
    }

    public void deletePoche(Long id, User utilisateur) {
        PocheSang poche = getPocheById(id);
        Hopital hopital = poche.getHopital();
        mouvementStockService.enregistrerMouvement(
                "SUPPRESSION",
                poche,
                hopital,
                utilisateur,
                "Suppression de la poche " + poche.getNumeroPoche()
        );
        pocheSangRepository.delete(poche);
    }

    public List<PocheSang> getPochesByHopital(Long hopitalId) {
        return pocheSangRepository.findByHopitalId(hopitalId);
    }

    public List<PocheSang> getPochesByGroupeSanguin(Long groupeSanguinId) {
        return pocheSangRepository.findByGroupeSanguinId(groupeSanguinId);
    }

    public List<PocheSang> getPochesByStatut(StatutPoche statut) {
        return pocheSangRepository.findByStatut(statut);
    }

    public int deletePochesPerimees(Long hopitalId, User utilisateur) {
        List<PocheSang> perimees = pocheSangRepository
                .findByHopitalIdAndDateExpirationBefore(hopitalId, LocalDate.now());
        int count = perimees.size();
        for (PocheSang poche : perimees) {
            mouvementStockService.enregistrerMouvement(
                    "EXPIRATION",
                    poche,
                    poche.getHopital(),
                    utilisateur,
                    "Suppression (expiration) de la poche " + poche.getNumeroPoche()
            );
        }
        pocheSangRepository.deleteAll(perimees);
        return count;
    }

    public List<PocheSang> getPochesProchesExpiration(Long hopitalId, int joursAvantExpiration) {
        LocalDate aujourd_hui = LocalDate.now();
        LocalDate dateLimite = aujourd_hui.plusDays(joursAvantExpiration);
        return pocheSangRepository.findByHopitalIdAndDateExpirationBetween(hopitalId, aujourd_hui, dateLimite);
    }

    public List<StockResumeDto> getStockResume(Long hopitalId) {
        List<GroupeSanguin> groupes = groupeSanguinRepository.findAll();
        List<StockResumeDto> resume = new ArrayList<>();

        for (GroupeSanguin groupe : groupes) {
            int disponible = pocheSangRepository
                    .findByHopitalIdAndGroupeSanguinIdAndStatut(hopitalId, groupe.getId(), StatutPoche.DISPONIBLE).size();
            int reserve = pocheSangRepository
                    .findByHopitalIdAndGroupeSanguinIdAndStatut(hopitalId, groupe.getId(), StatutPoche.RESERVE).size();
            int expire = pocheSangRepository
                    .findByHopitalIdAndGroupeSanguinIdAndStatut(hopitalId, groupe.getId(), StatutPoche.EXPIRE).size();

            resume.add(StockResumeDto.builder()
                    .groupeSanguin(groupe.getLibelle())
                    .quantiteDisponible(disponible)
                    .quantiteReservee(reserve)
                    .quantiteExpiree(expire)
                    .build());
        }
        return resume;
    }

    public List<StockResumeDto> getStockResumeGlobal() {
        List<GroupeSanguin> groupes = groupeSanguinRepository.findAll();
        List<StockResumeDto> resume = new ArrayList<>();

        for (GroupeSanguin groupe : groupes) {
            int disponible = pocheSangRepository
                    .findByStatut(StatutPoche.DISPONIBLE).stream()
                    .filter(p -> p.getGroupeSanguin().getId().equals(groupe.getId())).toList().size();
            int reserve = pocheSangRepository
                    .findByStatut(StatutPoche.RESERVE).stream()
                    .filter(p -> p.getGroupeSanguin().getId().equals(groupe.getId())).toList().size();
            int expire = pocheSangRepository
                    .findByStatut(StatutPoche.EXPIRE).stream()
                    .filter(p -> p.getGroupeSanguin().getId().equals(groupe.getId())).toList().size();

            resume.add(StockResumeDto.builder()
                    .groupeSanguin(groupe.getLibelle())
                    .quantiteDisponible(disponible)
                    .quantiteReservee(reserve)
                    .quantiteExpiree(expire)
                    .build());
        }
        return resume;
    }

    public List<RechercheSanguinDto> rechercherPochesDisponibles(Long groupeSanguinId, Long excludeHopitalId) {
        GroupeSanguin groupe = groupeSanguinRepository.findById(groupeSanguinId)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe sanguin non trouve avec l'id : " + groupeSanguinId));

        List<PocheSang> poches = pocheSangRepository
                .findByStatutAndGroupeSanguinIdAndHopitalIdNot(StatutPoche.DISPONIBLE, groupeSanguinId, excludeHopitalId);

        Map<Hopital, Long> quantiteParHopital = poches.stream()
                .filter(p -> p.getHopital() != null)
                .collect(Collectors.groupingBy(PocheSang::getHopital, Collectors.counting()));

        return quantiteParHopital.entrySet().stream()
                .map(entry -> {
                    Hopital hopital = entry.getKey();
                    int quantite = entry.getValue().intValue();
                    return RechercheSanguinDto.builder()
                            .nomHopital(hopital.getNom())
                            .hopitalId(hopital.getId())
                            .ville(hopital.getVille())
                            .telephone(hopital.getTelephone())
                            .groupeSanguin(groupe.getLibelle())
                            .quantiteDisponible(quantite)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
