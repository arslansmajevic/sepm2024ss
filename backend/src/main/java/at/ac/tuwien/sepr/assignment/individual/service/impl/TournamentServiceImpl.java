package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.ParticipationDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.RaceDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import at.ac.tuwien.sepr.assignment.individual.service.validator.TournamentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final HorseService horseService;
  private final TournamentValidator validator;
  private final BreedService breedService;
  private final RaceDao raceDao;
  private final TournamentMapper mapper;
  private final ParticipationDao participationDao;

  public TournamentServiceImpl(TournamentDao tournamentDao,
                               HorseService horseService,
                               TournamentValidator validator,
                               BreedService breedService,
                               RaceDao raceDao,
                               TournamentMapper mapper,
                               ParticipationDao participationDao) {
    this.tournamentDao = tournamentDao;
    this.horseService = horseService;
    this.validator = validator;
    this.breedService = breedService;
    this.raceDao = raceDao;
    this.mapper = mapper;
    this.participationDao = participationDao;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto tournamentSearchDto) {
    LOG.trace("tournament service searching({})", tournamentSearchDto);

    var tournaments = tournamentDao.search(tournamentSearchDto);
    return tournaments.stream()
            .map(mapper::entityToListDto)
            .sorted((t1, t2) -> t2.startDate().compareTo(t1.startDate()));
  }

  @Override
  public TournamentDetailDto create(TournamentCreateDto tournamentCreateDto) throws ValidationException, NotFoundException {
    LOG.trace("tournament service creating({})", tournamentCreateDto);

    validator.validateForCreate(tournamentCreateDto);

    // create the tournament
    var tournament = tournamentDao.create(tournamentCreateDto);

    // add participations and races
    participationDao.addParticipationsOnTournament(tournamentCreateDto, tournament.getId());
    raceDao.addRacesOnTournament(tournamentCreateDto, tournament.getId());

    // retrieve the horses
    var participants = horseService.getTournamentHorses(tournament.getId());

    // map to TournamentDetailDto
    return mapper.entityToTournamentDetailDto(tournament, participants);
  }

  @Override
  public TournamentStandingsDto getTournamentStanding(Long id) throws NotFoundException, ValidationException {
    LOG.trace("tournament service getting tournament standings on({})", id);

    Tournament tournament = tournamentDao.getTournament(id);
    TournamentDetailParticipantDto[] participants = horseService.getTournamentHorses(id);
    Collection<Race> races = raceDao.getHorseRaces(id);


    return mapper.mapTournamentStanding(tournament, participants, races);
  }

  @Override
  public TournamentStandingsDto createFirstRoundMatches(long tournamentId) throws ValidationException, NotFoundException, ConflictException {
    LOG.trace("tournament service creating first round matches on({})", tournamentId);

    var tournament = tournamentDao.getTournament(tournamentId);
    var participants = horseService.getTournamentHorses(tournamentId);

    var participantScores = calculateParticipantScores(participants, tournament);
    var sortedParticipants = sortParticipantsByScore(participants, participantScores);

    var races = raceDao.updateFirstRoundMatches(tournamentId, sortedParticipants.toArray(new TournamentDetailParticipantDto[0]));

    return mapper.mapTournamentStanding(tournament, participants, races);
  }

  @Override
  public TournamentStandingsDto updateStanding(long id, TournamentStandingsDto standingsDto) throws NotFoundException, ValidationException {

    LOG.trace("tournament service updating standing({}) on id({})", standingsDto, id);
    var participations = participationDao.getTournamentParticipations(id);
    validator.validateParticipations(standingsDto.participants(), participations);
    validator.validateUpdateStanding(id, standingsDto);
    validator.validateDoubleParticipantsFromTree(standingsDto.tree());

    var tournament = tournamentDao.getTournament(id);
    raceDao.updateRacesOfTournament(standingsDto);
    var races = raceDao.getHorseRaces(id);

    return mapper.mapTournamentStanding(tournament, standingsDto.participants(), races);
  }

  private Map<TournamentDetailParticipantDto, Long> calculateParticipantScores(TournamentDetailParticipantDto[] participants, Tournament tournament) {
    LOG.trace("tournament service calculating participant scores");
    Map<TournamentDetailParticipantDto, Long> participantScores = new HashMap<>();
    for (TournamentDetailParticipantDto participant : participants) {
      Collection<Tournament> lastYearTournamentsOfHorse = tournamentDao.findPreviousTournaments(participant, tournament);
      if (!lastYearTournamentsOfHorse.isEmpty()) {
        Long score = calculateScoreForParticipant(participant, lastYearTournamentsOfHorse);
        LOG.info("Horse ({} {}) received a rating of {} on tournament {}", participant.name(), participant.dateOfBirth(), score, tournament.getName());
        participantScores.put(participant, score);
      }
    }
    return participantScores;
  }

  private Long calculateScoreForParticipant(TournamentDetailParticipantDto participant, Collection<Tournament> tournaments) {
    LOG.trace("tournament service calculating participant score({})", participant);
    Long score = 0L;
    for (Tournament t : tournaments) {
      Long calculated = raceDao.evaluateTournamentScoreOnParticipant(participant, t);
      score += calculated != null ? calculated : 0;
    }
    return score;
  }

  private List<TournamentDetailParticipantDto> sortParticipantsByScore(TournamentDetailParticipantDto[] participants,
                                                                       Map<TournamentDetailParticipantDto, Long> participantScores) {
    LOG.trace("tournament service sorting participants({})", Arrays.stream(participants)
            .map(participant -> String.valueOf(participant.horseId()).concat(" " + participant.name()))
            .collect(Collectors.joining(", ")));
    List<TournamentDetailParticipantDto> sortedParticipants = new ArrayList<>(List.of(participants));
    sortedParticipants.sort((p1, p2) -> {
      long score1 = participantScores.getOrDefault(p1, 0L);
      long score2 = participantScores.getOrDefault(p2, 0L);
      // sort by score in decreasing order
      if (score1 != score2) {
        return Long.compare(score2, score1);
      } else {
        // if scores are equal, sort alphabetically by name
        return p1.name().compareToIgnoreCase(p2.name());
      }
    });
    LOG.trace("tournament service sorted participants({})", sortedParticipants);
    return sortedParticipants;
  }
}
