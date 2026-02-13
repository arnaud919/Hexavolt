package com.hexavolt.backend.service;

import com.hexavolt.backend.entity.Reservation;

import java.math.BigDecimal;

public interface ReservationPricingService {

    BigDecimal calculateAmount(Reservation reservation);
}

