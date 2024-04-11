package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"test", "datagen"})
// enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@DirtiesContext
public class ParticipationDaoTest extends TestBase {

  @Autowired
  ParticipationDao participationDao;

  @Test
  @DirtiesContext
  @Order(1)
  public void participationSizeOfATourmanent() throws Exception {

    var tournamentParticipations = participationDao.getTournamentParticipations(-1L);

    assertThat(tournamentParticipations).hasSize(8);
  }

  @Test
  @Order(2)
  public void unknownTournamentParticipations() throws Exception {

    var tournamentParticipations = participationDao.getTournamentParticipations(-210602L);

    assertThat(tournamentParticipations).hasSize(0);
  }

  @Test
  @Order(3)
  public void searchParticipationsForAHorse() throws Exception {

    List<Long> tournamentIdsToCheck = Arrays.asList(-9L, -8L, -7L, -6L, -5L);

    List<Participation> tournamentParticipations = participationDao.searchParticipations(-16L).stream().toList();

    List<Long> tournamentIds = tournamentParticipations.stream()
            .map(Participation::getTournamentId)
            .collect(Collectors.toList());

    assertThat(tournamentIds).containsExactlyElementsOf(tournamentIdsToCheck);
  }
}
