package dev.lobophf.application.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import dev.lobophf.application.common.PlanetConstants;

@DataJpaTest
public class PlanetRepositoryTest {

  @Autowired
  private PlanetRepository planetRepository;

  @Autowired
  private TestEntityManager testEntityManager;

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
  public void createPlanet_withExistingName_throwsException(){
    Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);
    testEntityManager.detach(planet);
    planet.setId(null);
    Assertions.assertThatThrownBy(() -> planetRepository.save(PlanetConstants.PLANET)).isInstanceOf(RuntimeException.class);
  }

}
