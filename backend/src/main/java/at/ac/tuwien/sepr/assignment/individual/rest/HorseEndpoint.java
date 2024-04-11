package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";
  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PostMapping()
  public ResponseEntity<HorseDetailDto> create(@RequestBody HorseCreateDto horseCreateDto) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", horseCreateDto);
    try {
      HorseDetailDto createdHorse = service.create(horseCreateDto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdHorse);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Parsed horse failed validation", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    } catch (ConflictException c) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Parsed horse data conflicts data in system", c);
      throw new ResponseStatusException(status, c.getMessage(), c);
    }
  }

  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable("id") long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException c) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse breed to update not found", c);
      throw new ResponseStatusException(status, c.getMessage(), c);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse to update failed on validation", v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity<HorseDetailDto> delete(@PathVariable("id") Long id) throws NotFoundException {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);

    try {
      HorseDetailDto result = service.delete(id);
      return ResponseEntity.status(HttpStatus.OK).body(result);
    } catch (NotFoundException n) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse with id %d is not present".formatted(id), n);
      throw new ResponseStatusException(status, n.getMessage(), n);
    } catch (ValidationException v) {
      HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
      logClientError(status, "Horse of id %d failed validation".formatted(id), v);
      throw new ResponseStatusException(status, v.getMessage(), v);
    }

  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
