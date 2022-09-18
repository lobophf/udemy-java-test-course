package dev.lobophf.application.web;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dev.lobophf.application.common.PlanetConstants;
import dev.lobophf.application.domain.PlanetService;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanetService planetService;

  @Test
  public void createPlanet_withValidData_returnsCreated() throws Exception {
    Mockito.when(planetService.create(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/planets")
            .content(objectMapper.writeValueAsString(PlanetConstants.PLANET)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }
}
