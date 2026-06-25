package com.sang.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.models.Hopital;

public interface HopitalRepository extends JpaRepository<Hopital, Long> {

    Optional<Hopital> findByNom(String nom);
}
