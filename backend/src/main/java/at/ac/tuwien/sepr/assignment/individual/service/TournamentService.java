package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with tournaments.
 */
public interface TournamentService {

  /**
   * Searches for tournaments based on the provided search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param tournamentSearchDto the parameters to use in searching for tournaments
   * @return a stream of tournament list DTOs representing the matching tournaments
   */
  Stream<TournamentListDto> search(TournamentSearchDto tournamentSearchDto);

  /**
   * Creates a new tournament using the data provided in {@code tournamentCreateDto}.
   *
   * @param tournamentCreateDto the data required to create the tournament
   * @return the created {@link TournamentDetailDto} representing the newly created tournament
   * @throws ValidationException if the data provided in the create DTO is invalid or incomplete
   */
  TournamentDetailDto create(TournamentCreateDto tournamentCreateDto) throws ValidationException, NotFoundException;

  /**
   * Retrieves the standings of a tournament identified by the provided ID.
   *
   * @param tournamentId the ID of the tournament
   * @return the standings of the tournament
   * @throws NotFoundException if the tournament with the given ID is not found or no horses, or races
   * @throws ValidationException if there are no races for this tournament - what should never be the case
   */
  TournamentStandingsDto getTournamentStanding(Long tournamentId) throws NotFoundException, ValidationException;

  /**
   * Creates the first round matches for a tournament identified by the provided ID.
   * Overwrites all previous data and creates a specific order of first round matches.
   *
   * @param tournamentId the ID of the tournament
   * @return the updated standings after creating the first round matches
   * @throws ValidationException if the tournament ID is invalid
   * @throws NotFoundException if the tournament with the given ID is not found or the participants are empty, or no races
   * @throws ConflictException if there are missing races during the update process
   */
  TournamentStandingsDto createFirstRoundMatches(long tournamentId) throws ValidationException, NotFoundException, ConflictException;

  /**
   * Updates the standings of a tournament with the provided ID.
   * This method allows partially saved tournaments.
   *
   * @param id the ID of the tournament
   * @param standingsDto the updated standings
   * @return the updated standings after the operation
   * @throws NotFoundException if the updated torunament is not retrieved
   * @throws ValidationException if the received parameters do not validate
   */
  TournamentStandingsDto updateStanding(long id, TournamentStandingsDto standingsDto) throws NotFoundException, ValidationException;
}
