package com.hexavolt.backend.service;

import java.util.List;

import com.hexavolt.backend.dto.PowerDTO;

public interface PowerService {
    List<PowerDTO> findAll();
}
