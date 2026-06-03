package com.hexavolt.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeChargingStationPhoto(MultipartFile file);

    String storeChargingStationVideo(MultipartFile file);
}
