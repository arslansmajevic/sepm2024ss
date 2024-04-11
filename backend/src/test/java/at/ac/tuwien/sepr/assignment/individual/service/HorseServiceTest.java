package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class HorseServiceTest extends TestBase {

  @Autowired
  HorseService horseService;

  @Test
  @Order(1)
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseService.search(searchDto);
    Assertions.assertNotNull(horses);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses)
        .extracting("id", "name", "sex", "dateOfBirth", "breed.name")
        .as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsOnly(
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony")
        );
  }

  @Test
  @Order(2)
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseService.search(searchDto);
    Assertions.assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(
            tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19),
                "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1),
                "Hanoverian"),
            tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5),
                "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10),
                "Welsh Cob"));
  }

  @Test
  @Order(3)
  public void updateValidHorse() throws Exception {
    HorseDetailDto requestHorse = new HorseDetailDto(
            -12L,
            "Charlie Changed",
            Sex.MALE,
            LocalDate.of(2012, 2, 11),
            2.34f,
            250.6f,
            new BreedDto(-1, "Welsh Cob")
    );

    var response = horseService.update(requestHorse);

    assertThat(response.name()).isEqualTo("Charlie Changed");
    assertThat(response.height()).isEqualTo(2.34f);
    assertThat(response.breed().id()).isEqualTo(-1);
  }

  @Test
  @Order(4)
  public void getTournamentHorses() throws Exception {
    var response = horseService.getTournamentHorses(-1L);
    assertThat(response).hasSize(8);
    TournamentDetailParticipantDto[] expectedArray = {
        new TournamentDetailParticipantDto(-8L, "Max", LocalDate.parse("2006-03-27"), 8, 1),
        new TournamentDetailParticipantDto(-7L, "Sophie", LocalDate.parse("2010-06-18"), 7, 1),
        new TournamentDetailParticipantDto(-6L, "Apollo", LocalDate.parse("2003-09-03"), 6, 1),
        new TournamentDetailParticipantDto(-5L, "Luna", LocalDate.parse("2012-11-22"), 5, 1),
        new TournamentDetailParticipantDto(-4L, "Thunder", LocalDate.parse("2008-07-15"), 4, 1),
        new TournamentDetailParticipantDto(-3L, "Bella", LocalDate.parse("2005-04-08"), 3, 1),
        new TournamentDetailParticipantDto(-2L, "Hugo", LocalDate.parse("2020-02-20"), 2, 1),
        new TournamentDetailParticipantDto(-1L, "Wendy", LocalDate.parse("2019-08-05"), 1, 1)
    };

    Assertions.assertArrayEquals(expectedArray, response);
  }

  @Test
  @Order(4)
  public void getUnknownTournamentHorses() throws Exception {
    Assertions.assertThrows(NotFoundException.class, () -> {
      horseService.getTournamentHorses(-210602L);
    });
  }

  @Test
  @DirtiesContext
  @Order(5)
  public void createHorseAndGetItById() throws Exception {
    HorseCreateDto requestHorse = new HorseCreateDto(
            "Charlie",
            Sex.FEMALE,
            LocalDate.of(2020, 2, 11),
            2.34f,
            250.6f,
            new BreedDto(-1, "Welsh Cob")
    );
    var response = horseService.create(requestHorse);

    assertThat(response.name()).isEqualTo("Charlie");
    assertThat(response.height()).isEqualTo(2.34f);
    assertThat(response.breed().id()).isEqualTo(-1);

    var response2 = horseService.getById(response.id());
    assertThat(response2.name()).isEqualTo("Charlie");
    assertThat(response2.height()).isEqualTo(2.34f);
    assertThat(response2.breed().id()).isEqualTo(-1);
  }

  @Test
  @DirtiesContext
  @Order(6)
  public void deleteHorseById() throws Exception {
    var response = horseService.delete(-29L);
    assertThat(response.name()).isEqualTo("Cody");
    assertThat(response.height()).isEqualTo(1.45f);
    assertThat(response.breed().id()).isEqualTo(-2L);
  }

  @Test
  @Order(7)
  public void deleteHorseThatIsPresentOnTournament() throws Exception {
    Assertions.assertThrows(ValidationException.class, () -> {
      horseService.delete(-1L);
    });
  }

  @Test
  @Order(8)
  public void updateHorseWithDateOfBirthAheadOfItsTournament() throws Exception {
    HorseDetailDto requestHorse = new HorseDetailDto(
            -1L,
            "Wendy",
            Sex.FEMALE,
            LocalDate.of(2023, 8, 5),
            1.45f,
            250.6f,
            new BreedDto(-2, "Welsh Pony"));
    Assertions.assertThrows(ValidationException.class, () -> {
      horseService.update(requestHorse);
    });
  }
}
