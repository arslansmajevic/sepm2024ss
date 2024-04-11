package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.mapper.BreedMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.BreedDao;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.stream.Stream;

import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BreedServiceImpl implements BreedService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private BreedDao dao;
  private BreedMapper mapper;

  public BreedServiceImpl(BreedDao dao, BreedMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public Stream<BreedDto> findBreedsByIds(Set<Long> breedIds) {
    LOG.trace("breed service finding breeds by ids({})", breedIds);
    return dao.findBreedsById(breedIds)
        .stream()
        .map(mapper::entityToDto);
  }

  @Override
  public Stream<BreedDto> search(BreedSearchDto searchParams) {
    LOG.trace("breed service searching({})", searchParams);
    return dao.search(searchParams)
        .stream()
        .map(mapper::entityToDto);
  }
}
