package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME_HORSE = "horse";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME_HORSE + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT  "
          + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
          + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME_HORSE + " h LEFT OUTER JOIN breed b ON (h.breed_id = b.id)"
          + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:sex IS NULL OR :sex = sex)"
          + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
          + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
          + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";

  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_INSERT = "INSERT INTO " + TABLE_NAME_HORSE
          + " (name, sex, date_of_birth, height, weight, breed_id)"
          + " VALUES (?, ?, ?, ?, ?, ?)";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME_HORSE
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME_HORSE + " WHERE id = ?";
  private static final String SQL_SEARCH_TOURNAMENT_HORSE = "SELECT  "
          + " h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\","
          + " h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME_HORSE + " h"
          + " LEFT JOIN breed b ON (h.breed_id = b.id)"
          + " LEFT JOIN participation p ON p.horse_id = h.id"
          + " WHERE p.tournament_id = ?";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;


  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("horse persistence getting horse by id({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("Horse is not present in the database");
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with the same id found");
    }

    return horses.get(0);
  }

  @Override
  public Horse create(HorseCreateDto horseCreateDto) {
    LOG.trace("horse persistence creating horse({})", horseCreateDto);
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, horseCreateDto.name());
      ps.setString(2, horseCreateDto.sex().toString());
      ps.setDate(3, Date.valueOf(horseCreateDto.dateOfBirth()));
      ps.setDouble(4, horseCreateDto.height());
      ps.setDouble(5, horseCreateDto.weight());

      if (horseCreateDto.breed() != null) {
        ps.setLong(6, horseCreateDto.breed().id());
      } else {
        ps.setNull(6, Types.INTEGER);
      }

      return ps;
    }, keyHolder);

    long newId = keyHolder.getKey().longValue();

    Horse createdHorse = new Horse()
            .setId(newId)
            .setName(horseCreateDto.name())
            .setDateOfBirth(horseCreateDto.dateOfBirth())
            .setSex(horseCreateDto.sex())
            .setHeight(horseCreateDto.height())
            .setWeight(horseCreateDto.weight());

    if (horseCreateDto.breed() != null) {
      createdHorse.setBreedId(horseCreateDto.breed().id());
    } else {
      createdHorse.setBreedId(null);
    }

    return createdHorse;
  }

  @Override
  public Horse delete(Long horseId) throws NotFoundException {
    LOG.trace("horse persistence deleting horse({})", horseId);

    Horse horse = null;
    try {
      horse = getById(horseId);
    } catch (NotFoundException n) {
      throw new NotFoundException("The horse could not be deleted because it is not present in the database", n);
    }

    jdbcTemplate.update(SQL_DELETE, horseId);

    return horse;
  }

  @Override
  public Collection<Horse> getTournamentHorses(Long tournamentId) throws NotFoundException {
    LOG.trace("horse persistence getting tournament horses on tournament({})", tournamentId);

    Collection<Horse> horses = jdbcTemplate.query(SQL_SEARCH_TOURNAMENT_HORSE, this::mapRow, tournamentId);

    if (horses.isEmpty()) {
      throw new NotFoundException("There are no horses defined for the requested tournament");
    }

    return horses;
  }


  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("horse persistence searching({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("horse persistence updating({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        horse.breed() != null ? horse.breed().id() : null,
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update " + horse.name() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        .setBreedId(horse.breed() != null ? horse.breed().id() : null);
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("horse persistence mapping horse({})", result);
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getObject("breed_id", Long.class))
        ;
  }
}
