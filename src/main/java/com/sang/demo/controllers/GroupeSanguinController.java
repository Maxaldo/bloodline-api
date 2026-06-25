package com.sang.demo.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sang.demo.models.GroupeSanguin;
import com.sang.demo.services.GroupeSanguinService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groupes-sanguins")
@RequiredArgsConstructor
public class GroupeSanguinController {

    private final GroupeSanguinService groupeSanguinService;

    @GetMapping
    public ResponseEntity<List<GroupeSanguin>> getAllGroupesSanguins() {
        return ResponseEntity.ok(groupeSanguinService.getAllGroupesSanguins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupeSanguin> getGroupeSanguinById(@PathVariable Long id) {
        return ResponseEntity.ok(groupeSanguinService.getGroupeSanguinById(id));
    }
}
