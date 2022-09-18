package dev.lobophf.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.lobophf.application.common.PlanetConstants;

import org.assertj.core.api.AssertionsForClassTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// @SpringBootTest(classes = PlanetService.class)
@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

  // @Autowired
  @InjectMocks
  private PlanetService planetService;

  // @MockBean
  @Mock
  private PlanetRepository planetRepository;

  @Test
  public void createPlanet_WithValidData_ReturnsPlanet() {
    Mockito.when(planetRepository.save(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);

    Planet sut = planetService.create(PlanetConstants.PLANET);
    Assertions.assertThat(sut).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void createPlanet_WithInvalidData_ThrowsExceptions() {
    Mockito.when(planetRepository.save(PlanetConstants.INVALID_PLANET)).thenThrow(RuntimeException.class);
    AssertionsForClassTypes.assertThatThrownBy(() -> planetService.create(PlanetConstants.INVALID_PLANET)).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void getPlanet_ByExistingId_ReturnsPlanet() {
    Mockito.when(planetRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(PlanetConstants.PLANET));

    Optional<Planet> sut = planetService.get(Mockito.anyLong());

    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void getPlanet_ByUnexistingId_ReturnsEmpty() {
    Mockito.when(planetRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    Optional<Planet> sut = planetService.get(Mockito.anyLong());

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void getPlanet_ByExistingName_ReturnsPlanet() {
    Mockito.when(planetRepository.findByName(Mockito.anyString())).thenReturn(Optional.of(PlanetConstants.PLANET));

    Optional<Planet> sut = planetService.getByName(Mockito.anyString());

    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void getPlanet_ByUnexistingName_ReturnsEmpty() {
    Mockito.when(planetRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());

    Optional<Planet> sut = planetService.getByName(Mockito.anyString());

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void listPlanets_ReturnsAllPlanets() {
    List<Planet> planets = new ArrayList<>(){
      {add(PlanetConstants.PLANET);}
    };
//
//    Example<Planet> query = QueryBuilder.makeQuery(new Planet(PlanetConstants.PLANET.getClimate(), PlanetConstants.PLANET.getTerrain()));
    Mockito.when(planetRepository.findAll(Mockito.any())).thenReturn(planets);

    List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());
    
    Assertions.assertThat(sut).isNotEmpty();
    Assertions.assertThat(sut).hasSize(1);
    Assertions.assertThat(sut.get(0)).isEqualTo(PlanetConstants.PLANET);
  }

  @Test
  public void listPlanets_ReturnsNoPlanets(){
    Mockito.when(planetRepository.findAll(Mockito.any())).thenReturn(Collections.emptyList());

    List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());

    Assertions.assertThat(sut).isEmpty();
  }

  @Test
  public void removePlanet_WithExistingId_doesNotThrowAnyException(){
    Assertions.assertThatCode(() -> planetService.remove(Mockito.anyLong())).doesNotThrowAnyException();
  }

  @Test
  public void removePlanet_WithOutExistingId_throwAnyException(){
    Mockito.doThrow(new RuntimeException()).when(planetRepository).deleteById(Mockito.anyLong());
    AssertionsForClassTypes.assertThatThrownBy(() -> planetService.remove(Mockito.anyLong()));
  }

}
