package com.hexavolt.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexavolt.backend.dto.AddressGeocodeResultDTO;
import com.hexavolt.backend.exception.BusinessException;
import com.hexavolt.backend.service.GeocodingService;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    private static final String GEOCODING_URL = "https://data.geopf.fr/geocodage/search";
    private static final double MIN_GEOCODING_SCORE = 0.75;
    private static final String EXPECTED_GEOCODING_TYPE = "housenumber";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeocodingServiceImpl(ObjectMapper objectMapper) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    @Override
    public AddressGeocodeResultDTO geocodeAddress(String address, String postalCode, String cityName) {
        if (address == null || address.isBlank()) {
            throw new BusinessException("L'adresse est obligatoire pour positionner le lieu.");
        }

        if (postalCode == null || postalCode.isBlank()) {
            throw new BusinessException("Le code postal est obligatoire pour positionner le lieu.");
        }

        if (cityName == null || cityName.isBlank()) {
            throw new BusinessException("La ville est obligatoire pour positionner le lieu.");
        }

        URI uri = UriComponentsBuilder
                .fromUriString(GEOCODING_URL)
                .queryParam("q", address)
                .queryParam("postcode", postalCode)
                .queryParam("limit", 1)
                .build()
                .encode()
                .toUri();

        System.out.println("URL géocodage = " + uri);

        try {
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode features = root.path("features");

            if (!features.isArray() || features.isEmpty()) {
                throw new BusinessException("Adresse introuvable. Vérifiez l'adresse, le code postal et la ville.");
            }

            JsonNode firstResult = features.get(0);
            JsonNode coordinates = firstResult.path("geometry").path("coordinates");
            JsonNode properties = firstResult.path("properties");

            if (!coordinates.isArray() || coordinates.size() < 2) {
                throw new BusinessException("Impossible de récupérer les coordonnées de cette adresse.");
            }

            Double longitude = coordinates.get(0).asDouble();
            Double latitude = coordinates.get(1).asDouble();

            String label = properties.path("label").asText();
            Double score = properties.path("score").asDouble();
            String type = properties.path("type").asText();
            String resultPostcode = properties.path("postcode").asText();
            String resultCity = properties.path("city").asText();

            if (!postalCode.equals(resultPostcode)) {
                throw new BusinessException("L'adresse trouvée ne correspond pas au code postal renseigné.");
            }

            if (score < MIN_GEOCODING_SCORE) {
                throw new BusinessException("Adresse introuvable ou trop imprécise. Vérifiez l'adresse renseignée.");
            }

            if (!EXPECTED_GEOCODING_TYPE.equals(type)) {
                throw new BusinessException(
                        "Adresse trop imprécise. Veuillez renseigner une adresse complète avec un numéro de rue.");
            }

            if (!normalize(cityName).equals(normalize(resultCity))) {
                throw new BusinessException("La ville trouvée ne correspond pas à la ville renseignée.");
            }

            return new AddressGeocodeResultDTO(label, latitude, longitude, score);

        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            System.out.println("Erreur API géocodage : " + exception.getStatusCode());
            System.out.println(exception.getResponseBodyAsString());

            throw new BusinessException("Erreur retournée par le service de géocodage.");
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BusinessException("Impossible de positionner l'adresse pour le moment.");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase()
                .replace("-", " ");
    }
}