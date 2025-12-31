package com.hexavolt.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.repository.CityRepository;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepo;

    public CityController(CityRepository cityRepo) {
        this.cityRepo = cityRepo;
    }

    @GetMapping
    public ResponseEntity<List<City>> getAll() {
        return ResponseEntity.ok(cityRepo.findAll());
    }

    @GetMapping("/search")
    public List<City> search(@RequestParam("q") String q) {
        String query = q == null ? "" : q.trim();
        if (query.length() < 2) {
            return List.of();
        }
        return cityRepo.findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc(query);
    }

}