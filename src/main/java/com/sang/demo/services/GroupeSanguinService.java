package com.sang.demo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.GroupeSanguin;
import com.sang.demo.repositories.GroupeSanguinRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupeSanguinService {

    private final GroupeSanguinRepository groupeSanguinRepository;

    public List<GroupeSanguin> getAllGroupesSanguins() {
        return groupeSanguinRepository.findAll();
    }

    public GroupeSanguin getGroupeSanguinById(Long id) {
        return groupeSanguinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe sanguin non trouve avec l'id : " + id));
    }
}
