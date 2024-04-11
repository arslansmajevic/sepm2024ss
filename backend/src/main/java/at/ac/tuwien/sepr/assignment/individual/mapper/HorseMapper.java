package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of breeds needs to contain the breed of {@code horse}.
   *
   * @param horse the horse to convert
   * @param breeds a map of breeds identified by their id, required for mapping horses
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, BreedDto> breeds) {
    LOG.trace("mapping a horse and breed to HorseListDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.getId(),
        horse.getName(),
        horse.getSex(),
        horse.getDateOfBirth(),
        breedFromMap(horse, breeds)
    );
  }

  /**
   * Convert a horse entity object to a {@link HorseListDto}.
   * The given map of breeds needs to contain the breed of {@code horse}.
   *
   * @param horse the horse to convert
   * @return the converted {@link HorseListDto}
   */
  public HorseDetailDto entityToDetailDto(Horse horse, Map<Long, BreedDto> breeds) {
    LOG.trace("mapping horse to horseDetailDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseDetailDto(
        horse.getId(),
        horse.getName(),
        horse.getSex(),
        horse.getDateOfBirth(),
        horse.getHeight(),
        horse.getWeight(),
        breedFromMap(horse, breeds)
    );
  }

  private BreedDto breedFromMap(Horse horse, Map<Long, BreedDto> map) {
    LOG.trace("mapping breed to BreedDto({})", horse);
    var breedId = horse.getBreedId();
    if (breedId == null) {
      return null;
    } else {
      return Optional.ofNullable(map.get(breedId))
          .orElseThrow(() -> new FatalException(
              "Saved horse with id " + horse.getId() + " refers to non-existing breed with id " + breedId));
    }
  }

  /**
   * Convert a detail DTO object to a create DTO object.
   *
   * @param horseCreateDto the detail DTO to convert
   * @return the converted create DTO
   */
  public HorseCreateDto detailDtoToCreateDto(HorseCreateDto horseCreateDto) {
    LOG.trace("mapping horseDetailDto to horseCreateDto({})", horseCreateDto);
    return new HorseCreateDto(horseCreateDto.name(),
            horseCreateDto.sex(),
            horseCreateDto.dateOfBirth(),
            horseCreateDto.height(),
            horseCreateDto.weight(),
            horseCreateDto.breed());
  }

  /**
   * Convert a collection of horses, races, and participations to an array of tournament detail thisParticipant DTOs.
   *
   * @param horses the collection of horses
   * @param races the collection of races
   * @param participations the collection of participations
   * @return an array of tournament detail thisParticipant DTOs
   */
  public TournamentDetailParticipantDto[] entityToTournamentDetailParticipantDto(Collection<Horse> horses,
                                                                                 Collection<Race> races,
                                                                                 Collection<Participation> participations) {

    LOG.trace("mapping horses to TournamentDetailParticipantDto({})", horses);
    Map<Long, TournamentDetailParticipantDto> resultMap = new HashMap<>();

    for (Horse h : horses) {
      for (Participation p : participations) {
        if (Objects.equals(h.getId(), p.getHorseId())) {
          resultMap.put(h.getId(), new TournamentDetailParticipantDto(
                  h.getId(),
                  h.getName(),
                  h.getDateOfBirth(),
                  p.getEntry(),
                  (int) roundReached(h.getId(), races)
          ));
        }
      }
    }

    // Convert the map values to an array
    return resultMap.values().toArray(new TournamentDetailParticipantDto[0]);
  }

  private long roundReached(Long horseId, Collection<Race> races) {
    LOG.trace("finding roundReached for a horse({})", horseId);

    long maxReached = 1;
    for (Race r : races) {
      if (horseId.equals(r.getFirstHorse()) || horseId.equals(r.getSecondHorse())) {
        maxReached = Math.max(maxReached, r.getRound());
      }
    }
    return maxReached;
  }
}
