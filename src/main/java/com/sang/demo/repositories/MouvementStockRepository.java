package com.sang.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.models.MouvementStock;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByHopital_IdOrderByDateMouvementDesc(Long hopitalId);
    List<MouvementStock> findByHopital_IdAndTypeMouvementOrderByDateMouvementDesc(Long hopitalId, String type);
    List<MouvementStock> findAllByOrderByDateMouvementDesc();
}

