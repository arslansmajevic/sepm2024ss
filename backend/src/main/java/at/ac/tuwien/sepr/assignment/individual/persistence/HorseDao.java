package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.Collection;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get the horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the horses where all given parameters match.
   */
  Collection<Horse> search(HorseSearchDto searchParameters);


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Creates a new horse using the data provided in {@code horseCreateDto} and stores it in the persistent data store.
   *
   * @param horseCreateDto the data required to create the horse
   * @return the created horse
   */
  Horse create(HorseCreateDto horseCreateDto);

  /**
   * Deletes a horse with the specified ID from the persistent data store.
   *
   * @param horseId the ID of the horse to delete
   * @return the deleted horse
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  Horse delete(Long horseId) throws NotFoundException;


  /**
   * Get the horses participating in a specific tournament identified by {@code tournamentId}.
   *
   * @param tournamentId the ID of the tournament
   * @return a collection of horses participating in the tournament
   */
  Collection<Horse> getTournamentHorses(Long tournamentId) throws ValidationException, NotFoundException;
}
