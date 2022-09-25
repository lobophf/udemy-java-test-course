package dev.lobophf.swplanetapi.domain;

import dev.lobophf.swplanetapi.common.PlanetConstants;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {
  @InjectMocks
  private PlanetService planetService;

  @Mock
  private PlanetRepository planetRepository;

  @Test
  public void createPlanet_WithValidData_ReturnsPlanet() {
    Mockito.when(planetRepository.save(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);

    Planet sut = planetService.create(PlanetConstants.PLANET);

    Assertions.assertThat(sut).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void createPlanet_WithInvalidData_ThrowsException() {
    Mockito.when(planetRepository.save(PlanetConstants.INVALID_PLANET)).thenThrow(RuntimeException.class);

    Assertions.assertThatThrownBy(() -> planetService.create(PlanetConstants.INVALID_PLANET)).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void getPlanet_ByExistingId_ReturnsPlanet() {
    Mockito.when(planetRepository.findById(1L)).thenReturn(Optional.of(PlanetConstants.PLANET));

    Optional<Planet> sut = planetService.get(1L);

    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void getPlanet_ByUnexistingId_ReturnsEmpty() {
    Mockito.when(planetRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Planet> sut = planetService.get(1L);

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void getPlanet_ByExistingName_ReturnsPlanet() {
    Mockito.when(planetRepository.findByName(PlanetConstants.PLANET.getName())).thenReturn(Optional.of(PlanetConstants.PLANET));

    Optional<Planet> sut = planetService.getByName(PlanetConstants.PLANET.getName());

    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void getPlanet_ByUnexistingName_ReturnsEmpty() {
    final String name = "Unexisting name";
    Mockito.when(planetRepository.findByName(name)).thenReturn(Optional.empty());

    Optional<Planet> sut = planetService.getByName(name);

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void listPlanets_ReturnsAllPlanets() {
    List<Planet> planets = new ArrayList<>() {
      {
        add(PlanetConstants.PLANET);
      }
    };
    Example<Planet> query = QueryBuilder.makeQuery(new Planet(PlanetConstants.PLANET.getClimate(), PlanetConstants.PLANET.getTerrain()));
    Mockito.when(planetRepository.findAll(query)).thenReturn(planets);

    List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());

    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut).hasSize(1);
    Assertions.assertThat(sut.get(0)).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void listPlanets_ReturnsNoPlanets() {
    Mockito.when(planetRepository.findAll(ArgumentMatchers.any())).thenReturn(Collections.emptyList());

    List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void removePlanet_WithExistingId_doesNotThrowAnyException() {
    Assertions.assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
  }

  @Test
  public void removePlanet_WithUnexistingId_ThrowsException() {
    Mockito.doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);

    Assertions.assertThatThrownBy(() -> planetService.remove(99L)).isInstanceOf(RuntimeException.class);
  }
}
