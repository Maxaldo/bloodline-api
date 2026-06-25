package com.sang.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sang.demo.models.GroupeSanguin;

public interface GroupeSanguinRepository extends JpaRepository<GroupeSanguin, Long> {

    Optional<GroupeSanguin> findByLibelle(String libelle);
}
