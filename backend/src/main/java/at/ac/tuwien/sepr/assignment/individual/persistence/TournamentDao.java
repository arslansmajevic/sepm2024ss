package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object for tournaments.
 * Implements access functionality to the application's persistent data store regarding tournaments.
 */
public interface TournamentDao {

  /**
   * Searches for tournaments based on the provided search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match if the given parameter is a substring of the tournament's name.
   *
   * @param searchParameters the parameters to use in searching for tournaments
   * @return a collection of tournaments where all given parameters match
   */
  Collection<Tournament> search(TournamentSearchDto searchParameters);

  /**
   * Creates a new tournament using the data provided in {@code tournamentCreateDto}
   * and stores it in the persistent data store.
   * It also adds all the participations and races based on the data provided in {@code tournamentCreateDto}.
   * Here, races will be added, that have not yet taken place.
   *
   * @param tournamentCreateDto the data required to create the tournament
   * @return the created tournament
   */
  Tournament create(TournamentCreateDto tournamentCreateDto);

  /**
   * Retrieves the tournament with the specified ID.
   *
   * @param tournamentId the ID of the tournament to retrieve
   * @return the tournament with the specified ID
   * @throws NotFoundException if the tournament with the given ID is not found
   */
  Tournament getTournament(Long tournamentId) throws NotFoundException;

  /**
   * Finds previous tournaments in which the specified participant has participated, excluding the provided tournament.
   *
   * @param participant the participant for which to find previous tournaments
   * @param tournament the current tournament to exclude from the search
   * @return a collection of previous tournaments in which the participant has participated
   */
  Collection<Tournament> findPreviousTournaments(TournamentDetailParticipantDto participant, Tournament tournament);
}
