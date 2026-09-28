package com.hexavolt.backend.controller;

import com.hexavolt.backend.entity.City;
import com.hexavolt.backend.repository.CityRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {

    @Mock
    private CityRepository cityRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CityController cityController = new CityController(cityRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(cityController)
                .build();
    }

    @Test
    void searchCitiesShouldReturnCitiesMatchingPrefix() throws Exception {
        City pantin = new City();
        pantin.setId(2L);
        pantin.setName("Pantin");

        City paris = new City();
        paris.setId(1L);
        paris.setName("Paris");

        when(cityRepository.findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc("Pa"))
                .thenReturn(List.of(pantin, paris));

        mockMvc.perform(get("/api/cities/search")
                        .param("q", "Pa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Pantin"))
                .andExpect(jsonPath("$[1].id").value(1))
                .andExpect(jsonPath("$[1].name").value("Paris"));

        verify(cityRepository)
                .findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc("Pa");
    }

    @Test
    void searchCitiesShouldReturnEmptyListWhenQueryIsTooShort() throws Exception {
        mockMvc.perform(get("/api/cities/search")
                        .param("q", "P"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchCitiesShouldTrimQueryBeforeSearching() throws Exception {
        City paris = new City();
        paris.setId(1L);
        paris.setName("Paris");

        when(cityRepository.findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc("Pa"))
                .thenReturn(List.of(paris));

        mockMvc.perform(get("/api/cities/search")
                        .param("q", "  Pa  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Paris"));

        verify(cityRepository)
                .findTop20ByNameStartingWithIgnoreCaseOrderByNameAsc("Pa");
    }
}