package com.hexavolt.backend.controller;

import com.hexavolt.backend.entity.ChargingStation;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.exception.ResourceNotFoundException;
import com.hexavolt.backend.repository.ChargingStationRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/public/stations")
public class ChargingStationMediaController {

    private static final Path PHOTO_DIR = Paths.get("uploads/charging-stations/photos")
            .toAbsolutePath()
            .normalize();

    private static final Path VIDEO_DIR = Paths.get("uploads/charging-stations/videos")
            .toAbsolutePath()
            .normalize();

    private final ChargingStationRepository stationRepo;

    public ChargingStationMediaController(ChargingStationRepository stationRepo) {
        this.stationRepo = stationRepo;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        ChargingStation station = findStationById(id);

        if (station.getPhotoName() == null || station.getPhotoName().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolveSafePath(PHOTO_DIR, station.getPhotoName());
        Resource resource = createResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return buildMediaResponse(path, resource);
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> getVideo(@PathVariable Long id) {
        ChargingStation station = findStationById(id);

        if (station.getVideoName() == null || station.getVideoName().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path path = resolveSafePath(VIDEO_DIR, station.getVideoName());
        Resource resource = createResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return buildMediaResponse(path, resource);
    }

    private ChargingStation findStationById(Long id) {
        return stationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borne introuvable."));
    }

    private Path resolveSafePath(Path directory, String fileName) {
        Path path = directory.resolve(fileName).normalize();

        if (!path.startsWith(directory)) {
            throw new BusinessException("Chemin de fichier invalide.");
        }

        return path;
    }

    private Resource createResource(Path path) {
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException exception) {
            throw new BusinessException("Fichier média invalide.");
        }
    }

    private ResponseEntity<Resource> buildMediaResponse(Path path, Resource resource) {
        try {
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(Files.size(path))
                    .body(resource);

        } catch (Exception exception) {
            throw new BusinessException("Impossible de lire le fichier média.");
        }
    }
}