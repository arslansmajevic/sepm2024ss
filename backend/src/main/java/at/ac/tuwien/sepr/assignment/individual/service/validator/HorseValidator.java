package at.ac.tuwien.sepr.assignment.individual.service.validator;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForUpdate(HorseDetailDto horse, Stream<BreedDto> existingBreed) throws ValidationException, ConflictException {
    LOG.trace("validating for update({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
      throw new ValidationException("Validation of horse for delete failed", validationErrors);
    }

    validateForCreate(new HorseCreateDto(
            horse.name(),
            horse.sex(),
            horse.dateOfBirth(),
            horse.height(),
            horse.weight(),
            horse.breed()), existingBreed);
  }

  public void validateForCreate(HorseCreateDto horseCreateDto, Stream<BreedDto> existingBreed) throws ValidationException, ConflictException {
    LOG.trace("validating for create({})", horseCreateDto);

    List<String> validationErrors = new ArrayList<>();

    if (horseCreateDto.name() == null) {
      validationErrors.add("Horse name was not defined");
    } else {
      if (horseCreateDto.name().length() > 255) {
        validationErrors.add("Horse name is longer than 255 characters");
      }
    }

    if (horseCreateDto.sex() == null) {
      validationErrors.add("Horse sex was not defined");
    } else {
      if (!horseCreateDto.sex().equals(Sex.FEMALE)) {
        if (!horseCreateDto.sex().equals(Sex.MALE)) {
          validationErrors.add("Horse sex is an invalid sex");
        }
      }
    }

    if (horseCreateDto.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth is not defined");
    } else {
      if (horseCreateDto.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Horse date of birth can not be set in future");
      }
    }

    if (horseCreateDto.height() == 0.0) {
      validationErrors.add("Horse height can not be 0 / is not defined");
    }

    if (horseCreateDto.height() >= 3) {
      validationErrors.add("Height value is too big");
    }

    if (horseCreateDto.height() < 0) {
      validationErrors.add("Height value cannot be 0 or negative");
    }

    if (horseCreateDto.weight() == 0.0) {
      validationErrors.add("Horse weight can not be 0 / is not defined");
    }

    if (horseCreateDto.weight() >= 2000) {
      validationErrors.add("Weight value is too big");
    }

    if (horseCreateDto.weight() < 0) {
      validationErrors.add("Weight can not be negative");
    }

    if (existingBreed != null) {
      int size = (int) existingBreed.count();
      if (size > 1) { // parsed id found more breeds, relevant error, should not ever happen
        validationErrors.add("There are too many breeds with this breed Id" + horseCreateDto.breed().id());
      }

      if (size == 0) {
        throw new ConflictException("Given breed is not availlable in the database", List.of("Given breed was not found"));
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for create failed", validationErrors);
    }
  }

  public void validateForDelete(Long id, Collection<Participation> tournamentParticipations) throws ValidationException {
    LOG.trace("validating delete for horse({})", id);

    List<String> validationErrors = new ArrayList<>();
    if (id == null) {
      validationErrors.add("Horse id is null");
    }

    if (!tournamentParticipations.isEmpty()) {
      validationErrors.add("Cannot delete horse because it is present on a tournament");
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for delete failed", validationErrors);
    }
  }
}
