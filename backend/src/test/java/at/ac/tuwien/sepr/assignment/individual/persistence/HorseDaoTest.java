package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class HorseDaoTest extends TestBase {

  @Autowired
  HorseDao horseDao;

  @Order(1)
  @Test
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseDao.search(searchDto);
    Assertions.assertNotNull(horses);
    assertThat(horses)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L),
            (new Horse())
                .setId(-21L)
                .setName("Bella")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2003, 7, 6))
                .setHeight(1.50f)
                .setWeight(580)
                .setBreedId(-19L),
            (new Horse())
                .setId(-2L)
                .setName("Hugo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2020, 2, 20))
                .setHeight(1.20f)
                .setWeight(320)
                .setBreedId(-20L));
  }

  @Order(2)
  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseDao.search(searchDto);
    Assertions.assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Horse())
                .setId(-24L)
                .setName("Rocky")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2018, 8, 19))
                .setHeight(1.42f)
                .setWeight(480)
                .setBreedId(-6L),
            (new Horse())
                .setId(-26L)
                .setName("Daisy")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2017, 12, 1))
                .setHeight(1.28f)
                .setWeight(340)
                .setBreedId(-9L),
            (new Horse())
                .setId(-31L)
                .setName("Leo")
                .setSex(Sex.MALE)
                .setDateOfBirth(LocalDate.of(2017, 3, 5))
                .setHeight(1.70f)
                .setWeight(720)
                .setBreedId(-8L),
            (new Horse())
                .setId(-32L)
                .setName("Luna")
                .setSex(Sex.FEMALE)
                .setDateOfBirth(LocalDate.of(2018, 10, 10))
                .setHeight(1.62f)
                .setWeight(670)
                .setBreedId(-19L));
  }

  @Order(3)
  @Test
  public void getHorseById() throws Exception {
    var horse = horseDao.getById(-32L);

    Assertions.assertNotNull(horse);
    assertThat(horse).isEqualToComparingFieldByField((new Horse())
            .setId(-32L)
            .setName("Luna")
            .setSex(Sex.FEMALE)
            .setDateOfBirth(LocalDate.of(2018, 10, 10))
            .setHeight(1.62f)
            .setWeight(670)
            .setBreedId(-19L));
  }

  @Order(4)
  @Test
  public void getHorseByIdNotFound() throws Exception {
    Assertions.assertThrows(NotFoundException.class, () -> {
      horseDao.getById(-210602L);
    });
  }

  @Order(5)
  @DirtiesContext
  @Test
  public void createNewHorse() throws Exception {
    HorseCreateDto newHorse = new HorseCreateDto(
            "Arslan Test",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.5f,
            300.5f,
            new BreedDto(-19, "Welsh Cob")
    );

    var result = horseDao.create(newHorse);

    assertThat(result).isEqualToComparingFieldByField((new Horse())
            .setId(result.getId())
            .setName("Arslan Test")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2002, 6, 21))
            .setHeight(2.5f)
            .setWeight(300.5f)
            .setBreedId(-19L));
  }

  @Order(6)
  @DirtiesContext
  @Test
  public void createNewHorseWithoutBreed() throws Exception {
    HorseCreateDto newHorse = new HorseCreateDto(
            "Arslan Test",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.5f,
            300.5f,
            null
    );

    var result = horseDao.create(newHorse);

    assertThat(result).isEqualToComparingFieldByField((new Horse())
            .setId(result.getId())
            .setName("Arslan Test")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2002, 6, 21))
            .setHeight(2.5f)
            .setWeight(300.5f));
  }

  @Order(7)
  @Test
  public void deleteHorse() throws Exception {
    Long horseId = -24L;

    var result = horseDao.delete(horseId);

    assertThat(result).isEqualToComparingFieldByField((new Horse())
            .setId(-24L)
            .setName("Rocky")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2018, 8, 19))
            .setHeight(1.42f)
            .setWeight(480)
            .setBreedId(-6L));
  }

  @Order(8)
  @Test
  public void deleteUnknownHorse() throws Exception {

    Assertions.assertThrows(NotFoundException.class, () -> {
      horseDao.delete(-210602L);
    });
  }

  @Order(9)
  @Test
  public void retrieveHorsesOfATournament() throws Exception {

    var result = horseDao.getTournamentHorses(-1L);

    assertThat(result)
            .hasSize(8)
            .usingRecursiveComparison()
            .isEqualTo(List.of(
                    new Horse()
                            .setId(-8L)
                            .setName("Max")
                            .setSex(Sex.MALE)
                            .setDateOfBirth(LocalDate.of(2006, 3, 27))
                            .setHeight(1.55f)
                            .setWeight(580)
                            .setBreedId(-6L),
                    new Horse()
                            .setId(-7L)
                            .setName("Sophie")
                            .setSex(Sex.FEMALE)
                            .setDateOfBirth(LocalDate.of(2010, 6, 18))
                            .setHeight(1.7f)
                            .setWeight(700)
                            .setBreedId(-5L),
                    new Horse()
                            .setId(-6L)
                            .setName("Apollo")
                            .setSex(Sex.MALE)
                            .setDateOfBirth(LocalDate.of(2003, 9, 3))
                            .setHeight(1.52f)
                            .setWeight(500)
                            .setBreedId(-4L),
                    new Horse()
                            .setId(-5L)
                            .setName("Luna")
                            .setSex(Sex.FEMALE)
                            .setDateOfBirth(LocalDate.of(2012, 11, 22))
                            .setHeight(1.65f)
                            .setWeight(650)
                            .setBreedId(-3L),
                    new Horse()
                            .setId(-4L)
                            .setName("Thunder")
                            .setSex(Sex.MALE)
                            .setDateOfBirth(LocalDate.of(2008, 7, 15))
                            .setHeight(1.6f)
                            .setWeight(600)
                            .setBreedId(-2L),
                    new Horse()
                            .setId(-3L)
                            .setName("Bella")
                            .setSex(Sex.FEMALE)
                            .setDateOfBirth(LocalDate.of(2005, 4, 8))
                            .setHeight(1.45f)
                            .setWeight(550)
                            .setBreedId(-1L),
                    new Horse()
                            .setId(-2L)
                            .setName("Hugo")
                            .setSex(Sex.MALE)
                            .setDateOfBirth(LocalDate.of(2020, 2, 20))
                            .setHeight(1.2f)
                            .setWeight(320)
                            .setBreedId(-20L),
                    new Horse()
                            .setId(-1L)
                            .setName("Wendy")
                            .setSex(Sex.FEMALE)
                            .setDateOfBirth(LocalDate.of(2019, 8, 5))
                            .setHeight(1.4f)
                            .setWeight(380)
                            .setBreedId(-15L)
            ));
  }

  @Order(10)
  @Test
  public void retrieveHorsesForAUnknownTournament() throws Exception {

    Assertions.assertThrows(NotFoundException.class, () -> {
      horseDao.getTournamentHorses(-210602L);
    });
  }

  @Order(11)
  @DirtiesContext
  @Test
  public void createUpdateAndVerifyHorse() throws Exception {

    HorseCreateDto newHorse = new HorseCreateDto(
            "Arslan Test",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.5f,
            300.5f,
            new BreedDto(-19, "Welsh Cob")
    );

    var result = horseDao.create(newHorse);
    Long resultId = result.getId();
    HorseDetailDto toUpdate = new HorseDetailDto(
            result.getId(),
            "Arslan Test But Changed",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.5f,
            300.5f,
            new BreedDto(-19, "Welsh Cob")
    );

    result = horseDao.update(toUpdate);

    assertThat(result).isEqualToComparingFieldByField((new Horse())
            .setId(resultId)
            .setName("Arslan Test But Changed")
            .setSex(Sex.MALE)
            .setDateOfBirth(LocalDate.of(2002, 6, 21))
            .setHeight(2.5f)
            .setWeight(300.5f)
            .setBreedId(-19L));

  }

  @Order(12)
  @Test
  public void updateHorseWithoutBreed() throws Exception {

    var result = horseDao.getById(-1L);
    var toUpdate = new HorseDetailDto(
            -1L,
            "Wendy",
            Sex.FEMALE,
            LocalDate.of(2019, 8, 5),
            1.4f,
            380,
            null
    );

    result = horseDao.update(toUpdate);
    Assertions.assertNull(result.getBreedId());
  }
}
