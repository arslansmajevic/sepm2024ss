package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Objects;

@Component
public class TournamentMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Maps a Tournament entity to its corresponding TournamentListDto.
   *
   * @param tournament the Tournament entity to map
   * @return the corresponding TournamentListDto
   */
  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("mapping tournament to TournamentListDto({})", tournament);

    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
            tournament.getId(),
            tournament.getName(),
            tournament.getDateOfStart(),
            tournament.getDateOfEnd()
    );
  }

  /**
   * Maps a Tournament entity and its participants to a TournamentDetailDto.
   *
   * @param tournamentResult the Tournament entity to map
   * @param participants     the array of TournamentDetailParticipantDto representing participants
   * @return the corresponding TournamentDetailDto
   */
  public TournamentDetailDto entityToTournamentDetailDto(Tournament tournamentResult, TournamentDetailParticipantDto[] participants) {
    LOG.trace("mapping tournament to TournamentDetailDto({})", tournamentResult);
    return new TournamentDetailDto(
            tournamentResult.getId(),
            tournamentResult.getName(),
            tournamentResult.getDateOfStart(),
            tournamentResult.getDateOfEnd(),
            participants
    );
  }

  /**
   * Maps a Tournament entity, its participants, and races to a TournamentStandingsDto including standings tree.
   *
   * @param tournament   the Tournament entity to map
   * @param participants the array of TournamentDetailParticipantDto representing participants
   * @param races        the collection of Race entities representing races
   * @return the corresponding TournamentStandingsDto including standings tree
   */
  public TournamentStandingsDto mapTournamentStanding(Tournament tournament,
                                                      TournamentDetailParticipantDto[] participants,
                                                      Collection<Race> races) {

    LOG.trace("mapping tournament to TournamentStandingsDto({}) with participants({}) and races({})", tournament, participants, races);
    TournamentStandingsTreeDto root = createTree(races, participants);


    return new TournamentStandingsDto(
            tournament.getId(),
            tournament.getName(),
            participants,
            root
    );
  }

  private TournamentStandingsTreeDto createTree(Collection<Race> races,
                                                TournamentDetailParticipantDto[] participants) {
    LOG.trace("creating tree ({})", races);
    for (Race r : races) {
      if (r.getRound() == 3) {
        return new TournamentStandingsTreeDto(
                this.findParticipant(participants, r.getWinner()),
                new TournamentStandingsTreeDto[]{
                        createLeftBranch(r.getPreviousRaceFirstHorse(), races, participants),
                        createRightBranch(r.getPreviousRaceSecondHorse(), races, participants)
                }
        );
      }
    }
    return null;
  }

  private TournamentStandingsTreeDto createLeftBranch(Long raceId,
                                                      Collection<Race> races,
                                                      TournamentDetailParticipantDto[] participants) {
    LOG.trace("creating left branch ({})", raceId);
    Race currentRace = findRace(races, raceId);
    if (currentRace.getPreviousRaceFirstHorse() == null
            && currentRace.getPreviousRaceSecondHorse() == null) {
      return new TournamentStandingsTreeDto(
              findParticipant(participants, currentRace.getWinner()),
              new TournamentStandingsTreeDto[]{
                  new TournamentStandingsTreeDto(
                          findParticipant(participants, currentRace.getFirstHorse()),
                          null
                  ),
                  new TournamentStandingsTreeDto(
                          findParticipant(participants, currentRace.getSecondHorse()),
                          null
                  ),
              }
      );
    }

    return new TournamentStandingsTreeDto(
            findParticipant(participants, currentRace.getWinner()),
            new TournamentStandingsTreeDto[]{
                    createLeftBranch(currentRace.getPreviousRaceFirstHorse(), races, participants),
                    createRightBranch(currentRace.getPreviousRaceSecondHorse(), races, participants)
            }
    );
  }

  private TournamentStandingsTreeDto createRightBranch(Long raceId,
                                                       Collection<Race> races,
                                                       TournamentDetailParticipantDto[] participants) {
    LOG.trace("creating right branch ({})", raceId);
    Race currentRace = findRace(races, raceId);

    if (currentRace.getPreviousRaceFirstHorse() == null
            && currentRace.getPreviousRaceSecondHorse() == null) {
      return new TournamentStandingsTreeDto(
              findParticipant(participants, currentRace.getWinner()),
              new TournamentStandingsTreeDto[]{
                  new TournamentStandingsTreeDto(
                          findParticipant(participants, currentRace.getFirstHorse()),
                          null
                  ),
                  new TournamentStandingsTreeDto(
                          findParticipant(participants, currentRace.getSecondHorse()),
                          null
                  ),
              }
      );
    }

    return new TournamentStandingsTreeDto(
            findParticipant(participants, currentRace.getWinner()),
            new TournamentStandingsTreeDto[]{
                    createLeftBranch(currentRace.getPreviousRaceFirstHorse(), races, participants),
                    createRightBranch(currentRace.getPreviousRaceSecondHorse(), races, participants)
            }
    );
  }

  // finds a thisParticipant with horse id -> race.getWinner id
  private TournamentDetailParticipantDto findParticipant(TournamentDetailParticipantDto[] participants, Long id) {
    LOG.trace("finding participant with id({})", id);
    if (id == null) {
      return null;
    }

    for (TournamentDetailParticipantDto participant : participants) {
      if (id.equals(participant.horseId())) {
        return participant;
      }
    }

    return null;
  }

  // finds Race with race id
  private Race findRace(Collection<Race> races, Long id) {
    LOG.trace("finding race with id({})", id);
    if (id == null) {
      return null;
    }
    for (Race r : races) {
      if (Objects.equals(r.getRaceId(), id)) {
        return r;
      }
    }
    return null;
  }
}
