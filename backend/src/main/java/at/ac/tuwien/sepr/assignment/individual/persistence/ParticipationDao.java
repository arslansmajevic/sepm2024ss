package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;

import java.util.Collection;

/**
 * Data Access Object for handling participations in tournaments.
 * Implements access functionality to the application's persistent data store regarding participations.
 */
public interface ParticipationDao {

  /**
   * Get the participations of horses in a specific tournament identified by {@code tournamentId}.
   *
   * @param tournamentId the ID of the tournament
   * @return a collection of participations of horses in the tournament
   */
  Collection<Participation> getTournamentParticipations(Long tournamentId);

  /**
   * Adds participations to a tournament based on the provided TournamentCreateDto and tournament ID.
   *
   * @param tournamentCreateDto the DTO containing information about the tournament and its participants
   * @param id the ID of the tournament
   */
  void addParticipationsOnTournament(TournamentCreateDto tournamentCreateDto, Long id);

  /**
   * Searches for participations of a horse identified by {@code horseId}.
   *
   * @param horseId the ID of the horse
   * @return a collection of participations of the horse
   */
  Collection<Participation> searchParticipations(Long horseId);
}
