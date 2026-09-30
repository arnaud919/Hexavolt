package com.hexavolt.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChargingStationUpdateDTO {

    @NotBlank(message = "Le nom de la borne est obligatoire.")
    @Size(max = 100, message = "Le nom de la borne ne doit pas dépasser 100 caractères.")
    private String name;

    @NotNull(message = "La puissance est obligatoire.")
    private Long powerId;

    @NotNull(message = "Le tarif horaire est obligatoire.")
    @DecimalMin(value = "0.01", message = "Le tarif horaire doit être supérieur à 0.")
    private BigDecimal hourlyRate;

    private String instruction;

    private boolean isCustom;

    @NotNull(message = "La latitude est obligatoire.")
    private Double latitude;

    @NotNull(message = "La longitude est obligatoire.")
    private Double longitude;

    @NotNull(message = "Le statut est obligatoire.")
    private Long statusId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPowerId() {
        return powerId;
    }

    public void setPowerId(Long powerId) {
        this.powerId = powerId;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

}
