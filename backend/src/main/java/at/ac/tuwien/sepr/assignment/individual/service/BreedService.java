package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedSearchDto;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Service for working with breeds.
 */
public interface BreedService {
  /**
   * Retrieve all stored breeds, that have one of the given IDs.
   * Note that if for one ID no breed is found, this method does not throw an error.
   *
   * @param breedIds the set of IDs to find breeds for.
   * @return a stream of all found breeds with an ID in {@code breedIds}
   */
  Stream<BreedDto> findBreedsByIds(Set<Long> breedIds);

  /**
   * Retrieve all stored breeds, that match the given parameters.
   * The parameters may include a limit on the amount of results to return.
   *
   * @param searchParams parameters to search breeds by
   * @return a stream of breeds matching the parameters
   */
  Stream<BreedDto> search(BreedSearchDto searchParams);
}
