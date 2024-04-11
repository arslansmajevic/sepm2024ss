package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.persistence.ParticipationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

@Repository
public class ParticipationJdbcDao implements ParticipationDao {

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME_PARTICIPATION = "participation";
  private static final String SQL_SEARCH_PARTICIPATIONS_ON_TOURNAMENT = "SELECT  "
          + " p.id as \"id\", p.tournament_id as \"tournament_id\", p.horse_id as \"horse_id\","
          + " p.entry_number as \"entry_number\""
          + " FROM " + TABLE_NAME_PARTICIPATION + " p "
          + " WHERE p.tournament_id = ?";
  private static final String SQL_INSERT_PARTICIPANT = "INSERT INTO " + TABLE_NAME_PARTICIPATION
          + " (tournament_id, horse_id, entry_number)"
          + " VALUES (?, ?, ?)";

  private static final String SQL_SEARCH_PARTICIPATIONS_ON_HORSE = "SELECT "
          + " p.id as \"id\", p.tournament_id as \"tournament_id\", p.horse_id as \"horse_id\", "
          + " p.entry_number as \"entry_number\" "
          + " FROM " + TABLE_NAME_PARTICIPATION + " p "
          + " WHERE "
          + " p.horse_id = ?";

  public ParticipationJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
    public Collection<Participation> getTournamentParticipations(Long tournamentId) {
    LOG.trace("participation persistence getting participations on tournament({})", tournamentId);

    return jdbcTemplate.query(SQL_SEARCH_PARTICIPATIONS_ON_TOURNAMENT, this::mapParticipationRow, tournamentId);
  }

  @Override
  public void addParticipationsOnTournament(TournamentCreateDto tournamentCreateDto, Long id) {
    LOG.trace("participation persistence creating participations on tournament({})", tournamentCreateDto);
    LOG.trace("tournament id{}", id);

    int entryCount = 1;
    KeyHolder keyHolderParticipant = new GeneratedKeyHolder();

    for (HorseSelectionDto participant : tournamentCreateDto.participants()) {

      int finalEntryCount = entryCount;
      jdbcTemplate.update(connection -> {

        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_PARTICIPANT, Statement.RETURN_GENERATED_KEYS);

        ps.setLong(1, id);
        ps.setLong(2, participant.id());
        ps.setLong(3, finalEntryCount);
        return ps;
      }, keyHolderParticipant);
      entryCount++;
    }
  }

  @Override
  public Collection<Participation> searchParticipations(Long horseId) {
    LOG.trace("participation persistence gathering participations on horse({})", horseId);

    return jdbcTemplate.query(SQL_SEARCH_PARTICIPATIONS_ON_HORSE, this::mapParticipationRow, horseId);
  }

  private Participation mapParticipationRow(ResultSet resultSet, int rownum) throws SQLException {
    LOG.trace("participation persistence mapping participation({})", resultSet);
    return new Participation()
            .setId(resultSet.getLong("id"))
            .setHorseId(resultSet.getLong("horse_id"))
            .setTournamentId(resultSet.getLong("tournament_id"))
            .setEntry((int) resultSet.getLong("entry_number"));
  }
}
