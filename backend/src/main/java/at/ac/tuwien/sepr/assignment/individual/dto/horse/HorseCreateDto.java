package at.ac.tuwien.sepr.assignment.individual.dto.horse;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;

public record HorseCreateDto(
        String name,
        Sex sex,
        LocalDate dateOfBirth,
        float height,
        float weight,
        BreedDto breed
) {
}
