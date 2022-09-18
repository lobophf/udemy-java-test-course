package dev.lobophf.application.domain;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import dev.lobophf.application.common.PlanetConstants;

@DataJpaTest
public class PlanetRepositoryTest {

  @Autowired
  private PlanetRepository planetRepository;

  @Autowired
  private TestEntityManager testEntityManager;

  @AfterEach
  public void afterEach() {
    PlanetConstants.PLANET.setId(null);
  }

  @Test
  public void createPlanet_withValidData_returnsPlanet() {
    Planet planet = planetRepository.save(PlanetConstants.PLANET);

    Planet sut = testEntityManager.find(Planet.class, planet.getId());

    Assertions.assertThat(sut).isNotNull();
    Assertions.assertThat(sut.getName()).isEqualTo(planet.getName());
    Assertions.assertThat(sut.getClimate()).isEqualTo(planet.getClimate());
    Assertions.assertThat(sut.getTerrain()).isEqualTo(planet.getTerrain());
  }

  @Test
  public void createPlanet_withValidData_throwsException() {
    Planet emptyPlanet = new Planet();
    Planet invalidPlanet = new Planet("", "", "");

    Assertions.assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
    Assertions.assertThatThrownBy(() -> planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
  }

  @Disabled
  @Test
  public void createPlanet_withExistingName_throwsException() {
    Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);
    testEntityManager.detach(planet);
    planet.setId(null);
    Assertions.assertThatThrownBy(() -> planetRepository.save(PlanetConstants.PLANET))
        .isInstanceOf(RuntimeException.class);
  }

  @Test
  public void getPlanet_byExistingId_returnsPlanet() {
    Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

    Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

    Assertions.assertThat(planetOpt).isNotEmpty();
    Assertions.assertThat(planetOpt.get()).isEqualTo(planet);
  }

  @Test
  public void getPlanet_byUnexisgingId_returnsEmpty() {

    Optional<Planet> planetOpt = planetRepository.findById(1L);
    Assertions.assertThat(planetOpt).isEmpty();

  }

  @Test
  public void getPlanet_byExistingName_returnsPlanet() {
    Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

    Optional<Planet> planetOpt = planetRepository.findByName(planet.getName());

    Assertions.assertThat(planetOpt).isNotEmpty();
    Assertions.assertThat(planetOpt.get()).isEqualTo(planet);
  }

  @Test
  public void getPlanet_byUnexisgingName_returnsEmpty() {

    Optional<Planet> planetOpt = planetRepository.findByName(PlanetConstants.PLANET.getName());
    Assertions.assertThat(planetOpt).isEmpty();

  }

  @Sql(scripts = "/import_planets.sql")
  @Test
  public void listPlanets_ReturnsFilteredPlanets() {
    Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
    Example<Planet> queryWithFilters = QueryBuilder
        .makeQuery(new Planet(PlanetConstants.TATOOINE.getClimate(), PlanetConstants.TATOOINE.getTerrain()));

    List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
    List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

    Assertions.assertThat(responseWithoutFilters).isNotEmpty();
    Assertions.assertThat(responseWithoutFilters).hasSize(3);

    Assertions.assertThat(responseWithFilters).isNotEmpty();
    Assertions.assertThat(responseWithFilters).hasSize(1);
    Assertions.assertThat(responseWithFilters.get(0)).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void listPlanets_ReturnsNoPlanets() {
    Example<Planet> query = QueryBuilder.makeQuery(new Planet());
    List<Planet> response = planetRepository.findAll(query);
    Assertions.assertThat(response).isEmpty();
  }

  @Test
  public void removePlane_withExistingId_removesPlanetFromDatabase() {
    Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

    planetRepository.deleteById(planet.getId());

    Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
    Assertions.assertThat(removedPlanet).isNull();
  }

  @Test
  public void removePlanet_withUnixintingId_throwExeption() {
    Assertions.assertThatThrownBy(() -> planetRepository.deleteById(1L))
        .isInstanceOf(EmptyResultDataAccessException.class);
  }
}
