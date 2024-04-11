package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.persistence.ParticipationDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.RaceDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.validator.HorseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao horseDao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final BreedService breedService;
  private final TournamentDao tournamentDao;
  private final RaceDao raceDao;
  private final ParticipationDao participationDao;

  public HorseServiceImpl(HorseDao horseDao,
                          HorseMapper mapper,
                          HorseValidator validator,
                          BreedService breedService,
                          TournamentDao tournamentDao,
                          RaceDao raceDao,
                          ParticipationDao participationDao) {
    this.horseDao = horseDao;
    this.mapper = mapper;
    this.validator = validator;
    this.breedService = breedService;
    this.tournamentDao = tournamentDao;
    this.raceDao = raceDao;
    this.participationDao = participationDao;
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("horse service searching({})", searchParameters);
    var horses = horseDao.search(searchParameters);
    // First get all breed ids…
    var breeds = horses.stream()
        .map(Horse::getBreedId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    // … then get the breeds all at once.
    var breedsPerId = breedMapForHorses(breeds);

    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, breedsPerId));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("horse service updating horse({})", horse);
    validator.validateForUpdate(horse,
            horse.breed() != null ? breedService.findBreedsByIds(Collections.singleton(horse.breed().id())) : null);


    var participations = participationDao.searchParticipations(horse.id());
    for (Participation p : participations) {
      var tournament = tournamentDao.getTournament(p.getTournamentId());

      if (horse.dateOfBirth().isAfter(tournament.getDateOfStart())) {
        List<String> validationErrors = new ArrayList<>();
        validationErrors.add("Tournament " + tournament.getName() + " has a start date of " + tournament.getDateOfStart());
        throw new ValidationException("Validation of horse for update failed", validationErrors);
      }
    }

    var updatedHorse = horseDao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("horse service getting horse by id({})", id);
    Horse horse = horseDao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public HorseDetailDto create(HorseCreateDto horseCreateDto) throws ValidationException, ConflictException {
    LOG.trace("horse service creating horse({})", horseCreateDto);

    validator.validateForCreate(horseCreateDto,
            horseCreateDto.breed() != null ? breedService.findBreedsByIds(Collections.singleton(horseCreateDto.breed().id())) : null);

    Horse horse = horseDao.create(horseCreateDto);
    var breeds = breedMapForSingleHorse(horse);

    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public HorseDetailDto delete(Long id) throws NotFoundException, ValidationException {
    LOG.trace("horse service deleting horse({})", id);

    validator.validateForDelete(id, participationDao.searchParticipations(id));

    Horse horse;
    try {
      horse = horseDao.delete(id);
    } catch (NotFoundException n) {
      throw new NotFoundException("No horse with id %d found".formatted(id));
    }

    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public TournamentDetailParticipantDto[] getTournamentHorses(Long tournamentId) throws NotFoundException, ValidationException {
    LOG.trace("horse service getting tournament horses on tournament id({})", tournamentId);

    var horses = horseDao.getTournamentHorses(tournamentId);

    var races = raceDao.getHorseRaces(tournamentId);
    var participation = participationDao.getTournamentParticipations(tournamentId);

    TournamentDetailParticipantDto[] result = mapper.entityToTournamentDetailParticipantDto(horses, races, participation);

    // Reverse the array
    List<TournamentDetailParticipantDto> resultList = Arrays.asList(result);
    Collections.reverse(resultList);
    return resultList.toArray(new TournamentDetailParticipantDto[0]);
  }

  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    LOG.trace("horse service getting breed map for single horse({})", horse);
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }

  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horse) {
    LOG.trace("horse service getting breed map for horses({})", horse);
    return breedService.findBreedsByIds(horse)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }
}
