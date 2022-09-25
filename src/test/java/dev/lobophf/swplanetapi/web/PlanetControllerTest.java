package dev.lobophf.swplanetapi.web;

import dev.lobophf.swplanetapi.common.PlanetConstants;
import org.hamcrest.Matchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import dev.lobophf.swplanetapi.domain.Planet;
import dev.lobophf.swplanetapi.domain.PlanetService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanetService planetService;

  @Test
  public void createPlanet_WithValidData_ReturnsCreated() throws Exception {
    Mockito.when(planetService.create(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/planets").content(objectMapper.writeValueAsString(PlanetConstants.PLANET))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }

  @Test
  public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
    Planet emptyPlanet = new Planet();
    Planet invalidPlanet = new Planet("", "", "");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/planets").content(objectMapper.writeValueAsString(emptyPlanet))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/planets").content(objectMapper.writeValueAsString(invalidPlanet))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
  }

  @Test
  public void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
    Mockito.when(planetService.create(ArgumentMatchers.any())).thenThrow(DataIntegrityViolationException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/planets").content(objectMapper.writeValueAsString(PlanetConstants.PLANET))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception {
    Mockito.when(planetService.get(1L)).thenReturn(Optional.of(PlanetConstants.PLANET));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }

  @Test
  public void getPlanet_ByUnexistingId_ReturnsNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception {
    Mockito.when(planetService.getByName(PlanetConstants.PLANET.getName())).thenReturn(Optional.of(PlanetConstants.PLANET));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/planets/name/" + PlanetConstants.PLANET.getName()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }

  @Test
  public void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/planets/name/1"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void listPlanets_ReturnsFilteredPlanets() throws Exception {
    Mockito.when(planetService.list(null, null)).thenReturn(PlanetConstants.PLANETS);
    Mockito.when(planetService.list(PlanetConstants.TATOOINE.getTerrain(), PlanetConstants.TATOOINE.getClimate())).thenReturn(List.of(PlanetConstants.TATOOINE));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/planets?"
                + String.format("terrain=%s&climate=%s", PlanetConstants.TATOOINE.getTerrain(), PlanetConstants.TATOOINE.getClimate())))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(PlanetConstants.TATOOINE));
  }

  @Test
  public void listPlanets_ReturnsNoPlanets() throws Exception {
    Mockito.when(planetService.list(null, null)).thenReturn(Collections.emptyList());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
  }

  @Test
  public void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.delete("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
    final Long planetId = 1L;

    Mockito.doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(planetId);

    mockMvc.perform(MockMvcRequestBuilders.delete("/planets/" + planetId))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
