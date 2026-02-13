package com.hexavolt.backend.service.impl;

import com.hexavolt.backend.entity.Reservation;
import com.hexavolt.backend.service.ReservationPricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
public class ReservationPricingServiceImpl
        implements ReservationPricingService {

    private static final BigDecimal HOURLY_RATE = new BigDecimal("5.00"); // tarif simple pour l’instant

    @Override
    public BigDecimal calculateAmount(Reservation reservation) {

        Duration duration = Duration.between(
                reservation.getStartDateTime(),
                reservation.getEndDateTime());

        long minutes = duration.toMinutes();

        // Arrondi au quart d’heure supérieur
        long roundedMinutes = ((minutes + 14) / 15) * 15;

        BigDecimal hours = BigDecimal.valueOf(roundedMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.UP);

        return hours.multiply(HOURLY_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
