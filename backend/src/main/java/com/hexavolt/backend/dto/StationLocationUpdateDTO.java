package com.hexavolt.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StationLocationUpdateDTO {

    @NotBlank(message = "Le surnom est obligatoire.")
    @Size(max = 100, message = "Le surnom ne doit pas dépasser 100 caractères.")
    private String nickname;

    @NotBlank(message = "L'adresse est obligatoire.")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères.")
    private String address;

    @NotBlank(message = "Le code postal est obligatoire.")
    @Size(max = 10, message = "Le code postal ne doit pas dépasser 10 caractères.")
    private String postalCode;

    @NotNull(message = "La ville est obligatoire.")
    private Long cityId;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}