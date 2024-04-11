package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

/**
 * Data Access Object for breeds.
 * Implements access functionality to the application's persistent data store regarding breeds.
 */
public interface BreedDao {

  /**
   * Retrieves all breeds stored in the data store.
   *
   * @return a collection of all breeds stored in the data store.
   */
  Collection<Breed> allBreeds();

  /**
   * Finds breeds based on the given set of breed IDs.
   *
   * @param breedIds a set of breed IDs to search for.
   * @return a collection of breeds matching the provided breed IDs.
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Searches for breeds based on the provided search parameters.
   *
   * @param searchParams the parameters to use in searching.
   * @return a collection of breeds that match the given search parameters.
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
