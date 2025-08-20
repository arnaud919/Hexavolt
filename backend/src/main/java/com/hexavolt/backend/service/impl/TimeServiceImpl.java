// com/hexavolt/backend/service/impl/TimeServiceImpl.java
package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.service.TimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC); // ⚠️ UTC pour uniformité
    }
}
