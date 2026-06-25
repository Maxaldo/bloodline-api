package com.sang.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sang.demo.dtos.PocheSangDto;
import com.sang.demo.dtos.RechercheSanguinDto;
import com.sang.demo.dtos.StockResumeDto;
import com.sang.demo.enums.StatutPoche;
import com.sang.demo.models.GroupeSanguin;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.PocheSang;
import com.sang.demo.models.User;
import com.sang.demo.repositories.GroupeSanguinRepository;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.PocheSangRepository;

@ExtendWith(MockitoExtension.class)
class PocheSangServiceTest {

    @Mock
    private PocheSangRepository pocheSangRepository;

    @Mock
    private GroupeSanguinRepository groupeSanguinRepository;

    @Mock
    private HopitalRepository hopitalRepository;

    @Mock
    private MouvementStockService mouvementStockService;

    @InjectMocks
    private PocheSangService pocheSangService;

    private GroupeSanguin buildGroupe(Long id, String libelle) {
        return GroupeSanguin.builder().id(id).libelle(libelle).build();
    }

    private Hopital buildHopital(Long id, String nom) {
        return Hopital.builder().id(id).nom(nom).build();
    }

    @Test
    void createPoche_ShouldReturnPoche() {
        PocheSangDto dto = new PocheSangDto();
        dto.setNumeroPoche("POCHE-001");
        dto.setDateCollecte(LocalDate.of(2026, 2, 20));
        dto.setDateExpiration(LocalDate.of(2026, 4, 20));
        dto.setStatut("DISPONIBLE");
        dto.setGroupeSanguinId(1L);
        dto.setHopitalId(1L);

        GroupeSanguin groupe = buildGroupe(1L, "O+");
        Hopital hopital = buildHopital(1L, "CHU de Cotonou");

        PocheSang savedPoche = PocheSang.builder()
                .id(1L)
                .numeroPoche("POCHE-001")
                .statut(StatutPoche.DISPONIBLE)
                .groupeSanguin(groupe)
                .hopital(hopital)
                .build();

        when(groupeSanguinRepository.findById(1L)).thenReturn(Optional.of(groupe));
        when(hopitalRepository.findById(1L)).thenReturn(Optional.of(hopital));
        when(pocheSangRepository.save(any(PocheSang.class))).thenReturn(savedPoche);

        User user = User.builder().id(10L).nom("Test").prenom("User").build();
        PocheSang result = pocheSangService.createPoche(dto, user);

        assertNotNull(result);
        assertEquals("POCHE-001", result.getNumeroPoche());
        verify(pocheSangRepository).save(any(PocheSang.class));
    }

    @Test
    void getPochesByHopital_ShouldReturnList() {
        PocheSang poche1 = PocheSang.builder().id(1L).numeroPoche("P-001").build();
        PocheSang poche2 = PocheSang.builder().id(2L).numeroPoche("P-002").build();

        when(pocheSangRepository.findByHopitalId(1L)).thenReturn(List.of(poche1, poche2));

        List<PocheSang> result = pocheSangService.getPochesByHopital(1L);

        assertEquals(2, result.size());
        verify(pocheSangRepository).findByHopitalId(1L);
    }

    @Test
    void getPochesByGroupeSanguin_ShouldReturnList() {
        PocheSang poche = PocheSang.builder().id(1L).numeroPoche("P-001").build();

        when(pocheSangRepository.findByGroupeSanguinId(1L)).thenReturn(List.of(poche));

        List<PocheSang> result = pocheSangService.getPochesByGroupeSanguin(1L);

        assertEquals(1, result.size());
        verify(pocheSangRepository).findByGroupeSanguinId(1L);
    }

    @Test
    void rechercherPochesDisponibles_ShouldExcludeHopital() {
        GroupeSanguin groupe = buildGroupe(1L, "O+");
        PocheSang pocheAutreHopital = PocheSang.builder()
                .id(1L)
                .numeroPoche("P-OTHER")
                .hopital(Hopital.builder()
                        .id(3L)
                        .nom("Autre Hopital")
                        .ville("Cotonou")
                        .telephone("90000000")
                        .build())
                .groupeSanguin(groupe)
                .build();

        when(groupeSanguinRepository.findById(1L)).thenReturn(Optional.of(groupe));
        when(pocheSangRepository.findByStatutAndGroupeSanguinIdAndHopitalIdNot(
                StatutPoche.DISPONIBLE, 1L, 2L))
                .thenReturn(List.of(pocheAutreHopital));

        List<RechercheSanguinDto> result = pocheSangService.rechercherPochesDisponibles(1L, 2L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getHopitalId());
        assertEquals("Autre Hopital", result.get(0).getNomHopital());
        assertEquals("Cotonou", result.get(0).getVille());
        assertEquals("90000000", result.get(0).getTelephone());
        assertEquals("O+", result.get(0).getGroupeSanguin());
        assertEquals(1, result.get(0).getQuantiteDisponible());
        verify(groupeSanguinRepository).findById(1L);
        verify(pocheSangRepository).findByStatutAndGroupeSanguinIdAndHopitalIdNot(
                StatutPoche.DISPONIBLE, 1L, 2L);
    }

    @Test
    void deletePochesPerimees_ShouldDeleteExpiredPoches() {
        PocheSang perimee1 = PocheSang.builder().id(1L).dateExpiration(LocalDate.of(2026, 1, 1)).build();
        PocheSang perimee2 = PocheSang.builder().id(2L).dateExpiration(LocalDate.of(2026, 1, 15)).build();
        List<PocheSang> perimees = List.of(perimee1, perimee2);

        when(pocheSangRepository.findByHopitalIdAndDateExpirationBefore(eq(1L), any(LocalDate.class)))
                .thenReturn(perimees);

        User user = User.builder().id(10L).nom("Test").prenom("User").build();
        int count = pocheSangService.deletePochesPerimees(1L, user);

        assertEquals(2, count);
        verify(pocheSangRepository).deleteAll(perimees);
    }

    @Test
    void getStockResume_ShouldReturnResumeByGroupeSanguin() {
        GroupeSanguin oPlus = buildGroupe(1L, "O+");
        GroupeSanguin aPlus = buildGroupe(2L, "A+");

        when(groupeSanguinRepository.findAll()).thenReturn(List.of(oPlus, aPlus));

        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 1L, StatutPoche.DISPONIBLE))
                .thenReturn(List.of(
                        PocheSang.builder().id(1L).build(),
                        PocheSang.builder().id(2L).build(),
                        PocheSang.builder().id(3L).build()));
        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 1L, StatutPoche.RESERVE))
                .thenReturn(List.of(PocheSang.builder().id(4L).build()));
        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 1L, StatutPoche.EXPIRE))
                .thenReturn(List.of());

        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 2L, StatutPoche.DISPONIBLE))
                .thenReturn(List.of(PocheSang.builder().id(5L).build()));
        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 2L, StatutPoche.RESERVE))
                .thenReturn(List.of());
        when(pocheSangRepository.findByHopitalIdAndGroupeSanguinIdAndStatut(1L, 2L, StatutPoche.EXPIRE))
                .thenReturn(List.of());

        List<StockResumeDto> result = pocheSangService.getStockResume(1L);

        assertEquals(2, result.size());

        StockResumeDto oPlusResume = result.get(0);
        assertEquals("O+", oPlusResume.getGroupeSanguin());
        assertEquals(3, oPlusResume.getQuantiteDisponible());
        assertEquals(1, oPlusResume.getQuantiteReservee());
        assertEquals(0, oPlusResume.getQuantiteExpiree());

        StockResumeDto aPlusResume = result.get(1);
        assertEquals("A+", aPlusResume.getGroupeSanguin());
        assertEquals(1, aPlusResume.getQuantiteDisponible());
        assertTrue(aPlusResume.getQuantiteReservee() == 0);
    }
}
