package com.sang.demo.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.enums.StatutPoche;
import com.sang.demo.models.PocheSang;

public interface PocheSangRepository extends JpaRepository<PocheSang, Long> {

    Optional<PocheSang> findByNumeroPoche(String numeroPoche);

    List<PocheSang> findByStatut(StatutPoche statut);

    List<PocheSang> findByHopitalId(Long hopitalId);

    List<PocheSang> findByGroupeSanguinId(Long groupeSanguinId);

    List<PocheSang> findByHopitalIdAndDateExpirationBefore(Long hopitalId, LocalDate date);

    List<PocheSang> findByHopitalIdAndDateExpirationBetween(Long hopitalId, LocalDate from, LocalDate to);

    List<PocheSang> findByHopitalIdAndStatut(Long hopitalId, StatutPoche statut);

    List<PocheSang> findByStatutAndGroupeSanguinIdAndHopitalIdNot(StatutPoche statut, Long groupeSanguinId, Long hopitalId);

    List<PocheSang> findByHopitalIdAndGroupeSanguinIdAndStatut(Long hopitalId, Long groupeSanguinId, StatutPoche statut);

    List<PocheSang> findByHopitalIdAndGroupeSanguinIdAndStatut(Long hopitalId, Long groupeSanguinId, StatutPoche statut, Pageable pageable);
}
