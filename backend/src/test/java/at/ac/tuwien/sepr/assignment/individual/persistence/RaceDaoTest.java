package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class RaceDaoTest extends TestBase {

  @Autowired
  RaceDao raceDao;

  @Test
  @DirtiesContext
  @Order(1)
  public void getRacesOfATournament() throws Exception {
    var result = raceDao.getHorseRaces(-1L);

    assertThat(result)
            .hasSize(7)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
              new Race()
                    .setRaceId(-7L)
                    .setFirstHorse(null)
                    .setSecondHorse(null)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(3L)
                    .setPreviousRaceFirstHorse(-5L)
                    .setPreviousRaceSecondHorse(-6L),
              new Race()
                    .setRaceId(-6L)
                    .setFirstHorse(null)
                    .setSecondHorse(null)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(2L)
                    .setPreviousRaceFirstHorse(-3L)
                    .setPreviousRaceSecondHorse(-4L),
              new Race()
                    .setRaceId(-5L)
                    .setFirstHorse(null)
                    .setSecondHorse(null)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(2L)
                    .setPreviousRaceFirstHorse(-1L)
                    .setPreviousRaceSecondHorse(-2L),
              new Race()
                    .setRaceId(-4L)
                    .setFirstHorse(-7L)
                    .setSecondHorse(-8L)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(1L)
                    .setPreviousRaceFirstHorse(null)
                    .setPreviousRaceSecondHorse(null),
              new Race()
                    .setRaceId(-3L)
                    .setFirstHorse(-5L)
                    .setSecondHorse(-6L)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(1L)
                    .setPreviousRaceFirstHorse(null)
                    .setPreviousRaceSecondHorse(null),
              new Race()
                    .setRaceId(-2L)
                    .setFirstHorse(-3L)
                    .setSecondHorse(-4L)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(1L)
                    .setPreviousRaceFirstHorse(null)
                    .setPreviousRaceSecondHorse(null),
              new Race()
                    .setRaceId(-1L)
                    .setFirstHorse(-1L)
                    .setSecondHorse(-2L)
                    .setWinner(null)
                    .setTournamentId(-1L)
                    .setRound(1L)
                    .setPreviousRaceFirstHorse(null)
                    .setPreviousRaceSecondHorse(null)
        );
  }

  @Test
  @Order(2)
  public void getRacesOfUnknownTournament() throws Exception {
    Assertions.assertThrows(ValidationException.class, () -> {
      raceDao.getHorseRaces(-210602L);
    });
  }

  @Test
  @DirtiesContext
  @Order(3)
  public void evaluateTournamentScoreOnParticipant() throws Exception {
    var tournament = new Tournament()
            .setId(-2L);

    var sophieTheWinner = new TournamentDetailParticipantDto(
            -7L,
            null,
            null,
            null,
            null
    );
    var sophieResult = raceDao.evaluateTournamentScoreOnParticipant(sophieTheWinner, tournament);
    assert (sophieResult.equals(5L));

    var wendyLostInSemiFinals = new TournamentDetailParticipantDto(
            -1L,
            null,
            null,
            null,
            null
    );
    var wendyResult = raceDao.evaluateTournamentScoreOnParticipant(wendyLostInSemiFinals, tournament);
    assert (wendyResult.equals(1L));

    var bellaLostInFinals = new TournamentDetailParticipantDto(
            -3L,
            null,
            null,
            null,
            null
    );
    var bellaResult = raceDao.evaluateTournamentScoreOnParticipant(bellaLostInFinals, tournament);
    assert (bellaResult.equals(3L));

    var hugoLostImmediately = new TournamentDetailParticipantDto(
            -2L,
            null,
            null,
            null,
            null
    );
    var hugoResult = raceDao.evaluateTournamentScoreOnParticipant(hugoLostImmediately, tournament);
    assert (hugoResult.equals(0L));
  }

  @Test
  @Order(4)
  public void unknownParametersOnScoreEvaluation() throws Exception {
    var unknownHorse = new TournamentDetailParticipantDto(
            -1235L,
            null,
            null,
            null,
            null
    );

    var tournament = new Tournament()
            .setId(-2L);

    var unknownHorseResult = raceDao.evaluateTournamentScoreOnParticipant(unknownHorse, tournament);

    assert (unknownHorseResult == null);

    var knownHorse = new TournamentDetailParticipantDto(
            -7L,
            null,
            null,
            null,
            null
    );

    tournament = new Tournament()
            .setId(-210602L);

    var unknownTournament = raceDao.evaluateTournamentScoreOnParticipant(knownHorse, tournament);

    assert (unknownTournament == null);
  }

}
