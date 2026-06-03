package com.hexavolt.backend.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.repository.ChargingStationRepository;

@RestController
@RequestMapping("/api/public/stations")
public class ChargingStationMediaController {

    private final ChargingStationRepository stationRepo;

    private static final Path PHOTO_DIR =
            Paths.get("uploads/charging-stations/photos");

    private static final Path VIDEO_DIR =
            Paths.get("uploads/charging-stations/videos");

    public ChargingStationMediaController(ChargingStationRepository stationRepo) {
        this.stationRepo = stationRepo;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) throws Exception {
        ChargingStation station = stationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charging station not found"));

        if (station.getPhotoName() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = PHOTO_DIR
                .resolve(station.getPhotoName())
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resource);
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> getVideo(@PathVariable Long id) throws Exception {
        ChargingStation station = stationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Charging station not found"));

        if (station.getVideoName() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = VIDEO_DIR
                .resolve(station.getVideoName())
                .normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resource);
    }
}
