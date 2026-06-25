package com.sang.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.HopitalDto;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.Hopital;
import com.sang.demo.repositories.HopitalRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HopitalService {

    private final HopitalRepository hopitalRepository;

    public List<Hopital> getAllHopitaux() {
        return hopitalRepository.findAll();
    }

    public Hopital getHopitalById(Long id) {
        return hopitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + id));
    }

    public Hopital createHopital(HopitalDto dto) {
        Hopital hopital = Hopital.builder()
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .ville(dto.getVille())
                .telephone(dto.getTelephone())
                .region(dto.getRegion())
                .build();
        return hopitalRepository.save(hopital);
    }

    public Hopital updateHopital(Long id, HopitalDto dto) {
        Hopital hopital = getHopitalById(id);
        hopital.setNom(dto.getNom());
        hopital.setAdresse(dto.getAdresse());
        hopital.setVille(dto.getVille());
        hopital.setTelephone(dto.getTelephone());
        hopital.setRegion(dto.getRegion());
        return hopitalRepository.save(hopital);
    }

    public void deleteHopital(Long id) {
        Hopital hopital = getHopitalById(id);
        hopitalRepository.delete(hopital);
    }
}
