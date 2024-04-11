package at.ac.tuwien.sepr.assignment.individual.dto.horse;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * DTO class for list of horses in search view.
 */
public record HorseListDto(
    Long id,
    String name,
    Sex sex,
    LocalDate dateOfBirth,
    BreedDto breed
) {
}
