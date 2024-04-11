package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
public class TournamentJdbcDao implements TournamentDao {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME_TOURNAMENT = "tournament";
  private static final String SQL_SELECT_SEARCH = "SELECT "
          + "t.id as \"id\", t.name as \"name\", t.date_of_start as \"date_of_start\", "
          + "t.date_of_end as \"date_of_end\" "
          + "FROM " + TABLE_NAME_TOURNAMENT + " t "
          + "WHERE (:name IS NULL OR UPPER(t.name) LIKE UPPER('%'||:name||'%')) "
          + "AND (:startDate IS NULL OR t.date_of_end >= :startDate) "
          + "AND (:endDate IS NULL OR t.date_of_start <= :endDate)";
  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_INSERT_TOURNAMENT = "INSERT INTO " + TABLE_NAME_TOURNAMENT
          + " (name, date_of_start, date_of_end)"
          + " VALUES (?, ?, ?)";

  private static final String SQL_SEARCH_TOURNAMENT_BY_ID = "SELECT "
          + " t.id as \"id\", t.name as \"name\", t.date_of_start as \"date_of_start\", "
          + " t.date_of_end as \"date_of_end\" "
          + " FROM " + TABLE_NAME_TOURNAMENT + " t "
          + " WHERE t.id = ?";

  private static final String SQL_SEARCH_PREVIOUS_TOURNAMENTS = "SELECT "
          + " t.id as \"id\", t.name as \"name\", t.date_of_start as \"date_of_start\", "
          + " t.date_of_end as \"date_of_end\" "
          + " FROM " + TABLE_NAME_TOURNAMENT + " t "
          + " JOIN participation p ON t.id = p.tournament_id "
          + " WHERE p.horse_id = ? "
          + " AND p.tournament_id IN ( "
          + " SELECT DISTINCT tournament_id FROM participation WHERE horse_id = ? "
          + " ) "
          + " AND t.date_of_end >= ? AND t.date_of_end < ?";

  public TournamentJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    LOG.trace("tournament persistence searching tournaments on parameters{}", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }

    var params = new BeanPropertySqlParameterSource(searchParameters);
    return jdbcNamed.query(query, params, this::mapRow);
  }

  @Override
  public Tournament create(TournamentCreateDto tournamentCreateDto) {
    LOG.trace("tournament persistence creating tournament{}", tournamentCreateDto);

    KeyHolder keyHolderTournament = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT_TOURNAMENT, Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, tournamentCreateDto.name());
      ps.setDate(2, Date.valueOf(tournamentCreateDto.startDate()));
      ps.setDate(3, Date.valueOf(tournamentCreateDto.endDate()));
      return ps;
    }, keyHolderTournament);

    long newTournamentId = keyHolderTournament.getKey().longValue();

    return new Tournament()
            .setId(newTournamentId)
            .setDateOfStart(tournamentCreateDto.startDate())
            .setName(tournamentCreateDto.name())
            .setDateOfEnd(tournamentCreateDto.endDate());
  }

  @Override
  public Tournament getTournament(Long tournamentId) throws NotFoundException {
    LOG.trace("tournament persistence getting tournament on id({})", tournamentId);

    List<Tournament> tournaments;

    tournaments = jdbcTemplate.query(SQL_SEARCH_TOURNAMENT_BY_ID, this::mapRow, tournamentId);

    if (tournaments.isEmpty()) {
      throw new NotFoundException("Requested tournament does not exist");
    }

    if (tournaments.size() > 1) {
      LOG.error("There is too many tournaments with the same id %d".formatted(tournamentId));
      throw new FatalException("Too many tournaments with this id");
    }

    return tournaments.get(0);
  }

  @Override
  public Collection<Tournament> findPreviousTournaments(TournamentDetailParticipantDto participant, Tournament tournament) {
    LOG.trace("tournament persistence finding previous tournaments for a participant({})", participant);
    return jdbcTemplate.query(SQL_SEARCH_PREVIOUS_TOURNAMENTS,
            this::mapRow,
            participant.horseId(),
            participant.horseId(),
            Date.valueOf(tournament.getDateOfStart().minusMonths(12)),
            Date.valueOf(tournament.getDateOfStart()));
  }

  private Tournament mapRow(ResultSet result, int rownum) throws SQLException {
    LOG.trace("tournament persistence mapping tournament({})", result);
    return new Tournament()
            .setId(result.getLong("id"))
            .setName(result.getString("name"))
            .setDateOfStart(result.getDate("date_of_start").toLocalDate())
            .setDateOfEnd(result.getDate("date_of_end").toLocalDate());
  }
}
