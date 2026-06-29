package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.service.FileStorageService;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final long MAX_PHOTO_SIZE = 5 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    private static final Path PHOTO_DIR = Paths.get("uploads/charging-stations/photos")
            .toAbsolutePath()
            .normalize();

    private static final Path VIDEO_DIR = Paths.get("uploads/charging-stations/videos")
            .toAbsolutePath()
            .normalize();

    @Override
    public String storeChargingStationPhoto(MultipartFile file) {
        validateFile(file, "image/", MAX_PHOTO_SIZE);
        return store(file, PHOTO_DIR);
    }

    @Override
    public String storeChargingStationVideo(MultipartFile file) {
        validateFile(file, "video/", MAX_VIDEO_SIZE);
        return store(file, VIDEO_DIR);
    }

    private void validateFile(MultipartFile file, String expectedType, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Le fichier est vide.");
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException("Le fichier est trop volumineux.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith(expectedType)) {
            throw new BusinessException("Type de fichier non autorisé.");
        }
    }

    private String store(MultipartFile file, Path directory) {
        try {
            Files.createDirectories(directory);

            String originalFilename = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "" : file.getOriginalFilename()
            );

            String extension = StringUtils.getFilenameExtension(originalFilename);
            String filename = UUID.randomUUID().toString();

            if (extension != null && !extension.isBlank()) {
                filename += "." + extension.toLowerCase();
            }

            Path target = directory.resolve(filename).normalize();

            if (!target.startsWith(directory)) {
                throw new BusinessException("Nom de fichier invalide.");
            }

            file.transferTo(target);

            return filename;

        } catch (IOException exception) {
            throw new RuntimeException("Impossible d'enregistrer le fichier.", exception);
        }
    }
}