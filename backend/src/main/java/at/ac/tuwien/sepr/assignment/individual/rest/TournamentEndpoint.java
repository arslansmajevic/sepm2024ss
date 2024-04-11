package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/tournaments";
  private final TournamentService service;
  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }
  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto tournamentSearchDto) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", tournamentSearchDto);

    return service.search(tournamentSearchDto);
  }

  @PostMapping
  public ResponseEntity<TournamentDetailDto> createTournament(@RequestBody TournamentCreateDto tournamentCreateDto) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", tournamentCreateDto);

    try {
      TournamentDetailDto createdTournament = service.create(tournamentCreateDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdTournament);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Parsed tournament failed validation", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Could not retrieve data", n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    }
  }

  @PutMapping("standings/firstRound/{id}")
  public TournamentStandingsDto generateFirstMatches(@PathVariable("id") long id) {
    LOG.info("PUT standings/firstRound/{}", id);

    try {
      return service.createFirstRoundMatches(id);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament with id %d is not present".formatted(id), n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Tournament with id %d failed on validation".formatted(id), v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    } catch (ConflictException c) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Action of tournament conflicts data", c);
      throw new ResponseStatusException(status, c.getMessage(), c);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<TournamentStandingsDto> getStanding(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);

    try {
      var result = service.getTournamentStanding(id);
      return ResponseEntity.status(HttpStatus.OK).body(result);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament with id %d is not present".formatted(id), n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Tournament with id %d failed on validation".formatted(id), v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }

  }

  @PutMapping("{id}")
  public ResponseEntity<TournamentStandingsDto> editStanding(@PathVariable("id") long id, @RequestBody TournamentStandingsDto standingsDto) {
    LOG.info("PUT " + BASE_PATH + "/{}", id);

    try {
      var result = service.updateStanding(id, standingsDto);
      return ResponseEntity.status(HttpStatus.OK).body(result);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Tournament with id %d is not present".formatted(id), n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Tournament with id %d failed on validation".formatted(id), v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
