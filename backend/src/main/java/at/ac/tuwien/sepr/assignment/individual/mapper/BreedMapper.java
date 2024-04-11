package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class BreedMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a Breed entity object to a {@link BreedDto}
   *
   * @param breed The Breed entity object to be converted.
   * @return the converted {@link BreedDto}
   */
  public BreedDto entityToDto(Breed breed) {
    LOG.trace("mapping breed to BreedDto({})", breed);
    return new BreedDto(breed.getId(), breed.getName());
  }
}
