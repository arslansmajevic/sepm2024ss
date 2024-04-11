package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Search for horses in the persistent data store matching all provided fields.
   * The name is considered a match, if the search string is a substring of the field in horse.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the horses where the given fields match.
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters);

  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (no name, name too long …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (breed does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;


  /**
   * Get the horse with given ID, with more detail information.
   * This includes the breed of the horse.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Creates a new horse using the data provided in {@code horseCreateDto} and stores it in the persistent data store.
   *
   * @param horseCreateDto the data required to create the horse
   * @return the detailed information of the created horse
   * @throws ValidationException if the data provided for creating the horse is invalid (no name, no sex etc.)
   * @throws ConflictException if the data given for the horse is in conflict with the data currently in system (invalid breed)
   */
  HorseDetailDto create(HorseCreateDto horseCreateDto) throws ValidationException, ConflictException;

  /**
   * Deletes a horse with the specified ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   * @return the detailed information of the deleted horse
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   * @throws ValidationException if there is an issue with the deletion process, such as the horse being associated with other entities
   */
  HorseDetailDto delete(Long id) throws NotFoundException, ValidationException;

  /**
   * Retrieves the horses participating in the tournament identified by the provided ID.
   *
   * @param tournamentId the ID of the tournament
   * @return an array of TournamentDetailParticipantDto representing the participating horses
   * @throws NotFoundException if there are no participants defined for the tournament - what should never be the case
   * @throws ValidationException if there are no races defined for the horses - should never be the case
   */
  TournamentDetailParticipantDto[] getTournamentHorses(Long tournamentId) throws NotFoundException, ValidationException;
}
