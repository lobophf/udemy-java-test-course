package dev.lobophf.application.web;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import dev.lobophf.application.common.PlanetConstants;
import dev.lobophf.application.domain.Planet;
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

  @Test
  public void createPlanet_withInvalidData_returnsBadRequest() throws Exception {

    Planet emptyPlanet = new Planet();
    Planet invalidPlanet = new Planet("", "", "");

    mockMvc
        .perform(MockMvcRequestBuilders.post("/planets")
            .content(objectMapper.writeValueAsString(emptyPlanet)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    mockMvc
        .perform(MockMvcRequestBuilders.post("/planets")
            .content(objectMapper.writeValueAsString(invalidPlanet)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
  }

  @Test
  public void createPlanet_withExistingName_returnsConflict() throws Exception {
    Mockito.when(planetService.create(Mockito.any())).thenThrow(DataIntegrityViolationException.class);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/planets")
            .content(objectMapper.writeValueAsString(PlanetConstants.PLANET)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  public void getPlanet_byExistingId_returns() throws Exception {
    Mockito.when(planetService.get(Mockito.anyLong())).thenReturn(Optional.of(PlanetConstants.PLANET));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }

  @Test
  public void getPlanet_byUnExistingId_returnsNotFound() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void getPlanet_byExistingName_returns() throws Exception {
    Mockito.when(planetService.getByName(Mockito.anyString())).thenReturn(Optional.of(PlanetConstants.PLANET));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets/name/" + PlanetConstants.PLANET.getName()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
  }

  @Test
  public void getPlanet_byUnExistingName_returnsNotFound() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets/name/dummyName"))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Disabled
  @Test
  public void listPlanets_returnsFilteredPlanets() throws Exception {
    Mockito.when(planetService.list(null, null)).thenReturn(PlanetConstants.PLANETS);
    Mockito.when(planetService.list(PlanetConstants.TATOOINE.getTerrain(), PlanetConstants.TATOOINE.getClimate()))
        .thenReturn(List.of(PlanetConstants.TATOOINE));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets?" + String.format("terrain=%s&climate=%s",
            PlanetConstants.TATOOINE.getTerrain(),
            PlanetConstants.TATOOINE.getClimate())))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(PlanetConstants.TATOOINE));
  }

  @Disabled
  @Test
  public void listPlanets_returnsNoPlanets() throws Exception {
    Mockito.when(planetService.list(null, null)).thenReturn(Collections.emptyList());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
  }

  @Test
  public void removePlanet_withExistingId_returnNoContent() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.delete("/planets/1"))
      .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Disabled
  @Test
  public void removePlanet_withUnexistingId_returnsNotFound() throws Exception{
    final long PLANET_ID = 1L;
    Mockito.doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(PLANET_ID);

    mockMvc.perform(MockMvcRequestBuilders.delete("/planets/" + PLANET_ID)).andExpect(MockMvcResultMatchers.status().isNotFound());
  }

}
