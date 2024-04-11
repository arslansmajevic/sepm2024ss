package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class TournamentDaoTest extends TestBase {

  @Autowired
  TournamentDao tournamentDao;

  @Test
  @Order(1)
  public void searchTournamentsContaining2016InGivenTimeRange() throws Exception {
    var tournaments = tournamentDao.search(new TournamentSearchDto(
            "2016",
            LocalDate.of(2016, 1, 1),
            LocalDate.of(2016, 6, 15),
            null
    ));

    Assertions.assertNotNull(tournaments);
    assertThat(tournaments)
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                    (new Tournament())
                            .setId(-7L)
                            .setName("2016 Tournament Mostar 3rd")
                            .setDateOfStart(LocalDate.of(2016, 5, 1))
                            .setDateOfEnd(LocalDate.of(2016, 6, 1)),
                    (new Tournament())
                            .setId(-6L)
                            .setName("2016 Tournament Mostar 2nd")
                            .setDateOfStart(LocalDate.of(2016, 3, 1))
                            .setDateOfEnd(LocalDate.of(2016, 4, 1)),
                    (new Tournament())
                            .setId(-5L)
                            .setName("2016 Tournament Mostar 1st")
                            .setDateOfStart(LocalDate.of(2016, 1, 1))
                            .setDateOfEnd(LocalDate.of(2016, 2, 1)));
  }

  @Test
  @Order(2)
  public void retrieveTournament() throws Exception {
    var tournament = tournamentDao.getTournament(-5L);
    assertThat(tournament)
            .usingRecursiveComparison()
            .isEqualTo((new Tournament())
                    .setId(-5L)
                    .setName("2016 Tournament Mostar 1st")
                    .setDateOfStart(LocalDate.of(2016, 1, 1))
                    .setDateOfEnd(LocalDate.of(2016, 2, 1)));
  }

  @Test
  @Order(3)
  public void retrieveTournamentWithUnknownId() throws Exception {
    Assertions.assertThrows(NotFoundException.class, () -> {
      tournamentDao.getTournament(-210602L);
    });
  }

  @Test
  @Order(4)
  public void findPreviousTournamentsOfAHorse() throws Exception {
    var participant = new TournamentDetailParticipantDto(
            -16L,
            "Jack",
            LocalDate.of(2014, 10, 25),
            1,
            1
    );
    var tournament = new Tournament()
            .setId(-7L)
            .setName("2016 Tournament Mostar 3rd")
            .setDateOfStart(LocalDate.of(2016, 5, 1))
            .setDateOfEnd(LocalDate.of(2016, 6, 1));

    var tournaments = tournamentDao.findPreviousTournaments(participant, tournament);
    Assertions.assertNotNull(tournaments);
    assertThat(tournaments)
            .hasSize(2)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                    (new Tournament())
                            .setId(-6L)
                            .setName("2016 Tournament Mostar 2nd")
                            .setDateOfStart(LocalDate.of(2016, 3, 1))
                            .setDateOfEnd(LocalDate.of(2016, 4, 1)),
                    (new Tournament())
                            .setId(-5L)
                            .setName("2016 Tournament Mostar 1st")
                            .setDateOfStart(LocalDate.of(2016, 1, 1))
                            .setDateOfEnd(LocalDate.of(2016, 2, 1)));

    var noTournaments = tournamentDao.findPreviousTournaments(participant, (new Tournament())
            .setId(-5L)
            .setName("2016 Tournament Mostar 1st")
            .setDateOfStart(LocalDate.of(2016, 1, 1))
            .setDateOfEnd(LocalDate.of(2016, 2, 1)));

    assertThat(noTournaments).hasSize(0);
  }
}
