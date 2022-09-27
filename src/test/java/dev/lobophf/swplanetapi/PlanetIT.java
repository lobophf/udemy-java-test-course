package dev.lobophf.swplanetapi;

import org.junit.jupiter.api.Test;
import dev.lobophf.swplanetapi.common.PlanetConstants;
import dev.lobophf.swplanetapi.domain.Planet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.assertj.core.api.Assertions;

@ActiveProfiles("it")
@Sql(scripts = { "/import_planets.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = { "/remove_planets.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PlanetIT {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void createPlanet_ReturnsCreated() {
    ResponseEntity<Planet> sut = restTemplate.postForEntity("/planets", PlanetConstants.PLANET, Planet.class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    Assertions.assertThat(sut.getBody().getId()).isNotNull();
    Assertions.assertThat(sut.getBody().getName()).isEqualTo(PlanetConstants.PLANET.getName());
    Assertions.assertThat(sut.getBody().getClimate()).isEqualTo(PlanetConstants.PLANET.getClimate());
    Assertions.assertThat(sut.getBody().getTerrain()).isEqualTo(PlanetConstants.PLANET.getTerrain());
  }

  @Test
  public void getPlanet_ReturnsPlanet() {
    ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(sut.getBody()).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void getPlanetByName_returnsPlanet() {
    ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/" + PlanetConstants.TATOOINE.getName(),
        Planet.class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(sut.getBody()).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void listPlanets_returnsAllPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets/", Planet[].class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(sut.getBody()).hasSize(3);
    Assertions.assertThat(sut.getBody()[0]).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void listPlanets_byClimate_returnsPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate
        .getForEntity("/planets?climate=" + PlanetConstants.TATOOINE.getClimate(), Planet[].class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(sut.getBody()).hasSize(1);
    Assertions.assertThat(sut.getBody()[0]).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void listPlanets_byTerrain_returnsPlanets() {
    ResponseEntity<Planet[]> sut = restTemplate
        .getForEntity("/planets?terrain=" + PlanetConstants.TATOOINE.getTerrain(), Planet[].class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.OK);
    Assertions.assertThat(sut.getBody()).hasSize(1);
    Assertions.assertThat(sut.getBody()[0]).isEqualTo(PlanetConstants.TATOOINE);
  }

  @Test
  public void removePlanet_returnsNoContent() {
    ResponseEntity<Void> sut = restTemplate.exchange("/planets/" + PlanetConstants.TATOOINE.getId(), HttpMethod.DELETE,
        null,
        Void.class);
    Assertions.assertThat(sut.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }
}
