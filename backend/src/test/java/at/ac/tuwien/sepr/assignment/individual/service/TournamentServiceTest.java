package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class TournamentServiceTest extends TestBase {

  @Autowired
  TournamentService tournamentService;

  @Test
  @Order(1)
  public void getByIdEmptyTournamentWithOnlyFirstRoundMatches() throws Exception {
    var tournamentId = -1L;
    var standings = tournamentService.getTournamentStanding(tournamentId);
    System.out.println(standings);

    assertThat(standings.name()).isEqualTo("2023 Wien Race");
    assertThat(standings.participants().length).isEqualTo(8);
    Assertions.assertNotNull(standings);
    assertThat(standings.tree().branches()[0].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Wendy");
    assertThat(standings.tree().branches()[0].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Hugo");
    assertThat(standings.tree().branches()[0].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Bella");
    assertThat(standings.tree().branches()[0].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Thunder");
    assertThat(standings.tree().branches()[1].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Luna");
    assertThat(standings.tree().branches()[1].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Apollo");
    assertThat(standings.tree().branches()[1].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(standings.tree().branches()[1].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Max");
  }

  @Test
  @Order(2)
  public void getByIdEmptyTournamentWithOnlySecondRoundMatches() throws Exception {
    var tournamentId = -2L;
    var standings = tournamentService.getTournamentStanding(tournamentId);

    assertThat(standings.name()).isEqualTo("2022 Sarajevo Race");

    assertThat(standings.tree().thisParticipant().name()).isEqualTo("Sophie");
    assertThat(standings.tree().branches()[0].thisParticipant().name()).isEqualTo("Bella");
    assertThat(standings.tree().branches()[1].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(standings.tree().branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Wendy");
    assertThat(standings.tree().branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Bella");
    assertThat(standings.tree().branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Luna");
    assertThat(standings.tree().branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(standings.tree().branches()[0].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Wendy");
    assertThat(standings.tree().branches()[0].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Hugo");
    assertThat(standings.tree().branches()[0].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Bella");
    assertThat(standings.tree().branches()[0].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Thunder");
    assertThat(standings.tree().branches()[1].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Luna");
    assertThat(standings.tree().branches()[1].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Apollo");
    assertThat(standings.tree().branches()[1].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(standings.tree().branches()[1].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Max");
  }

  @Test
  @Order(3)
  public void updateStandingOfATournament() throws Exception {
    var tournamentId = -2L;
    var standings = tournamentService.getTournamentStanding(tournamentId);

    var newTree = new TournamentStandingsTreeDto(
            standings.tree().branches()[0].thisParticipant(),
            standings.tree().branches()
    );
    var newStanding = new TournamentStandingsDto(
            standings.id(),
            standings.name(),
            standings.participants(),
            newTree
    );

    var updatedStandings = tournamentService.updateStanding(tournamentId, newStanding);

    assertThat(updatedStandings.tree().thisParticipant().name()).isEqualTo("Bella");
    assertThat(updatedStandings.tree().branches()[0].thisParticipant().name()).isEqualTo("Bella");
    assertThat(updatedStandings.tree().branches()[1].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(updatedStandings.tree().branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Wendy");
    assertThat(updatedStandings.tree().branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Bella");
    assertThat(updatedStandings.tree().branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Luna");
    assertThat(updatedStandings.tree().branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(updatedStandings.tree().branches()[0].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Wendy");
    assertThat(updatedStandings.tree().branches()[0].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Hugo");
    assertThat(updatedStandings.tree().branches()[0].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Bella");
    assertThat(updatedStandings.tree().branches()[0].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Thunder");
    assertThat(updatedStandings.tree().branches()[1].branches()[0].branches()[0].thisParticipant().name()).isEqualTo("Luna");
    assertThat(updatedStandings.tree().branches()[1].branches()[0].branches()[1].thisParticipant().name()).isEqualTo("Apollo");
    assertThat(updatedStandings.tree().branches()[1].branches()[1].branches()[0].thisParticipant().name()).isEqualTo("Sophie");
    assertThat(updatedStandings.tree().branches()[1].branches()[1].branches()[1].thisParticipant().name()).isEqualTo("Max");

  }

  @Test
  @Order(4)
  public void switchHorsesOnWinnerRoundExpectingAValidationError() throws Exception {
    var tournamentId = -2L;
    var standings = tournamentService.getTournamentStanding(tournamentId);

    var newTree = new TournamentStandingsTreeDto(
            standings.tree().thisParticipant(),
            new TournamentStandingsTreeDto[]{
                new TournamentStandingsTreeDto(
                        standings.tree().branches()[1].thisParticipant(),
                        standings.tree().branches()[0].branches()
                ),
                new TournamentStandingsTreeDto(
                        standings.tree().branches()[0].thisParticipant(),
                        standings.tree().branches()[1].branches()
                )
            }
    );
    var newStanding = new TournamentStandingsDto(
            standings.id(),
            standings.name(),
            standings.participants(),
            newTree
    );

    Assertions.assertThrows(ValidationException.class, () -> {
      tournamentService.updateStanding(tournamentId, newStanding);
    });

  }

  @Test
  @Order(4)
  public void updateStandingWithAssignmentOfAWinnerWithoutPreviousRacesResultsInError() throws Exception {
    var tournamentId = -1L;
    var standings = tournamentService.getTournamentStanding(tournamentId);

    var newTree = new TournamentStandingsTreeDto(
            new TournamentDetailParticipantDto(
                    -8L,
                    "Sophie",
                    LocalDate.of(2010, 6, 18),
                    7,
                    1
            ),
            standings.tree().branches()
    );
    var newStanding = new TournamentStandingsDto(
            standings.id(),
            standings.name(),
            standings.participants(),
            newTree
    );

    Assertions.assertThrows(ValidationException.class, () -> {
      tournamentService.updateStanding(tournamentId, newStanding);
    });

  }

  @Test
  @Order(5)
  public void updateStandingWithAssignmentOfAWinnerInTheMiddleOfTheTreeWithoutPreviousRacesResultsInError() throws Exception {
    var tournamentId = -1L;
    var standings = tournamentService.getTournamentStanding(tournamentId);

    var newTree = new TournamentStandingsTreeDto(
            null,
            new TournamentStandingsTreeDto[]{
                new TournamentStandingsTreeDto(
                        standings.tree().branches()[0].thisParticipant(),
                        standings.tree().branches()[0].branches()
                ),
                new TournamentStandingsTreeDto(
                        new TournamentDetailParticipantDto(
                                -8L,
                                "Sophie",
                                LocalDate.of(2010, 6, 18),
                                7,
                                1
                        ),
                        standings.tree().branches()[1].branches()
                )
            }
    );
    var newStanding = new TournamentStandingsDto(
            standings.id(),
            standings.name(),
            standings.participants(),
            newTree
    );

    Assertions.assertThrows(ValidationException.class, () -> {
      tournamentService.updateStanding(tournamentId, newStanding);
    });
  }
}
