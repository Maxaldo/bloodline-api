package com.sang.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sang.demo.dtos.ProfilResponseDto;
import com.sang.demo.enums.RoleName;
import com.sang.demo.exceptions.ResourceNotFoundException;
import com.sang.demo.models.Hopital;
import com.sang.demo.models.User;
import com.sang.demo.repositories.HopitalRepository;
import com.sang.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HopitalRepository hopitalRepository;

    public List<ProfilResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toProfilResponse)
                .collect(Collectors.toList());
    }

    public ProfilResponseDto getUserById(Long id) {
        User user = findUserById(id);
        return toProfilResponse(user);
    }

    public ProfilResponseDto changeRole(Long id, String role) {
        User user = findUserById(id);
        user.setRole(RoleName.valueOf(role));
        User updated = userRepository.save(user);
        return toProfilResponse(updated);
    }

    public ProfilResponseDto assignHopital(Long id, Long hopitalId) {
        User user = findUserById(id);
        if (user.getRole() == RoleName.ADMIN) {
            throw new IllegalArgumentException("Un administrateur ne peut pas etre assigne a un hopital");
        }
        Hopital hopital = hopitalRepository.findById(hopitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hopital non trouve avec l'id : " + hopitalId));
        user.setHopital(hopital);
        User updated = userRepository.save(user);
        return toProfilResponse(updated);
    }

    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec l'id : " + id));
    }

    private ProfilResponseDto toProfilResponse(User user) {
        return ProfilResponseDto.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .dateCreation(user.getDateCreation())
                .role(user.getRole())
                .hopital(user.getHopital() != null ? user.getHopital().getNom() : null)
                .hopitalId(user.getHopital() != null ? user.getHopital().getId() : null)
                .build();
    }
}
