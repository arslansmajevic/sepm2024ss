package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Race;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.persistence.RaceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RaceJdbcDao implements RaceDao {
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME_RACE = "race";
  private static final String SQL_SEARCH_RACES_ON_TOURNAMENT = "SELECT  "
          + " r.id as \"id\", r.first_horse as \"first_horse\", r.second_horse as \"second_horse\","
          + " r.winner as \"winner\", r.tournament_id as \"tournament_id\","
          + " r.round as \"round\", r.previous_race_first_horse as \"previous_race_first_horse\","
          + " r.previous_race_second_horse as \"previous_race_second_horse\""
          + " FROM " + TABLE_NAME_RACE + " r"
          + " WHERE tournament_id = ?";
  private static final String SQL_SELECT_RACE_BY_ID = "SELECT  "
          + " r.id as \"id\", r.first_horse as \"first_horse\", r.second_horse as \"second_horse\","
          + " r.winner as \"winner\", r.tournament_id as \"tournament_id\","
          + " r.round as \"round\", r.previous_race_first_horse as \"previous_race_first_horse\","
          + " r.previous_race_second_horse as \"previous_race_second_horse\""
          + " FROM " + TABLE_NAME_RACE + " r"
          + " WHERE id = ?";
  private static final String SQL_INSERT_RACE = "INSERT INTO " + TABLE_NAME_RACE
          + " (first_horse, second_horse, winner, tournament_id, round, previous_race_first_horse, previous_race_second_horse)"
          + " VALUES (?, ?, ?, ?, ?, ?, ?)";

  private static final String SQL_UPDATE_FIRST_RACES = "UPDATE " + TABLE_NAME_RACE
          + " SET first_horse = ?, "
          + " second_horse = ?, "
          + " winner = ? "
          + " WHERE round = ? AND tournament_id = ? AND id = ?";

  private static final String SQL_UPDATE_OTHER_RACES = "UPDATE " + TABLE_NAME_RACE
          + " SET first_horse = ?, "
          + " second_horse = ?, "
          + " winner = ?"
          + " WHERE tournament_id = ? AND id = ?";
  private static final String SQL_SELECT_FIRST_RACE_IDS = "SELECT id FROM " + TABLE_NAME_RACE
          + " WHERE tournament_id = :tournamentId AND previous_race_first_horse IS NULL AND previous_race_second_horse IS NULL";
  private static final String SQL_SELECT_RACE_ID_TOURNAMENT_WINNER = "SELECT id FROM " + TABLE_NAME_RACE
          + " WHERE tournament_id = :tournamentId AND round = 3";
  private static final String SQL_SELECT_OTHER_RACE_IDS = "SELECT id FROM " + TABLE_NAME_RACE
          + " WHERE tournament_id = :tournamentId AND previous_race_first_horse IS NOT NULL AND previous_race_second_horse IS NOT NULL";

  private static final String SQL_HORSE_SCORE_ON_TOURNAMENT = "SELECT "
          + " SUM(CASE "
          + " WHEN r.round = 3 AND r.winner = ? THEN 5 "
          + " WHEN r.round = 3 AND r.winner != ? THEN 3 "
          + " WHEN r.round = 2 AND r.winner != ? THEN 1 "
          + " ELSE 0 "
          + " END) AS \"score\""
          + " FROM " + TABLE_NAME_RACE + " r "
          + " JOIN "
          + " tournament t ON r.tournament_id = t.id "
          + " WHERE t.id = ? "
          + " AND (? IN (r.first_horse, r.second_horse))";
  private static final String SQL_UPDATE_RACE = "UPDATE " + TABLE_NAME_RACE
          + " SET first_horse = ?, "
          + " second_horse = ?, "
          + " winner = ? "
          + " WHERE tournament_id = ? AND id = ?";

  public RaceJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Collection<Race> getHorseRaces(Long tournamentId) throws ValidationException {
    LOG.trace("race persistence getting horse races on tournament id({})", tournamentId);

    Collection<Race> races = jdbcTemplate.query(SQL_SEARCH_RACES_ON_TOURNAMENT, this::mapRaceRow, tournamentId);

    if (races.isEmpty()) {
      throw new ValidationException("There are no races defined for the requested tournament", List.of("Races are not defined"));
    }

    return races;
  }

  @Override
  public Collection<Race> updateFirstRoundMatches(long tournamentId,
                                                  TournamentDetailParticipantDto[] participants) throws ValidationException, ConflictException {
    LOG.trace("race persistence updating first round races on tournament id({})", tournamentId);
    LOG.trace("race persistence update matches participants({})",
            Arrays.stream(participants)
            .map(participant -> String.valueOf(participant.horseId()).concat(" " + participant.name()))
            .collect(Collectors.joining(", ")));

    // searching the race ids for start
    List<Long> ids = jdbcNamed.query(SQL_SELECT_FIRST_RACE_IDS, new MapSqlParameterSource("tournamentId", tournamentId),
            (resultSet, rowNum) -> resultSet.getLong("id"));
    List<String> validationErrors = new ArrayList<>();

    if (ids.isEmpty()) {
      validationErrors.add("There are missing mandatory first round races for this tournament");
      throw new ConflictException("Database Conflict", validationErrors);
    }

    // signaling that these are the test data races and hence the negative ids
    if (ids.iterator().next() < 0) {
      Collections.reverse(ids);
    }

    // updating the races, assigning the horses
    int counter = 0;
    for (Long id : ids) {
      // no need here for exceptions
      jdbcTemplate.update(SQL_UPDATE_FIRST_RACES,
              participants[counter].horseId(),
              participants[participants.length - 1 - counter].horseId(),
              null,
              1,
              tournamentId,
              id
      );
      counter++;
    }

    // getting all other races id that are not first
    ids = jdbcNamed.query(SQL_SELECT_OTHER_RACE_IDS, new MapSqlParameterSource("tournamentId", tournamentId),
            (resultSet, rowNum) -> resultSet.getLong("id"));

    if (ids.isEmpty()) {
      validationErrors.add("There are missing mandatory other round races for this tournament");
      throw new ConflictException("Database Conflict", validationErrors);
    }

    // signaling that these are the test data races and hence the negative ids
    if (ids.iterator().next() < 0) {
      Collections.reverse(ids);
    }
    // updating all other races (setting everything as null)
    for (Long id : ids) {
      jdbcTemplate.update(SQL_UPDATE_OTHER_RACES,
              null,
              null,
              null,
              tournamentId,
              id
      );
    }
    return getHorseRaces(tournamentId);
  }

  @Override
  public void addRacesOnTournament(TournamentCreateDto tournamentCreateDto, Long id) {
    LOG.trace("race persistence creating races of tournament{}", tournamentCreateDto);
    LOG.trace("tournament id{}", id);

    KeyHolder keyHolderRace = new GeneratedKeyHolder();
    List<Long> secondRound = new ArrayList<>();

    // first round
    for (int i = 0; i < tournamentCreateDto.participants().length; i = i + 2) {
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_RACE, Statement.RETURN_GENERATED_KEYS);

        ps.setNull(1, Types.INTEGER);
        ps.setNull(2, Types.INTEGER);
        ps.setNull(3, Types.INTEGER);
        ps.setLong(4, id);
        ps.setLong(5, 1);
        ps.setNull(6, Types.INTEGER);
        ps.setNull(7, Types.INTEGER);

        return ps;
      }, keyHolderRace);

      secondRound.add(keyHolderRace.getKey().longValue());
    }

    List<Long> lastRound = new ArrayList<>();

    // second round
    for (int i = 0; i < 4; i = i + 2) {

      int finalI = i;
      jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(SQL_INSERT_RACE, Statement.RETURN_GENERATED_KEYS);

        ps.setNull(1, Types.INTEGER);
        ps.setNull(2, Types.INTEGER);
        ps.setNull(3, Types.INTEGER);
        ps.setLong(4, id);
        ps.setLong(5, 2);
        ps.setLong(6, secondRound.get(finalI));
        ps.setLong(7, secondRound.get(finalI + 1));

        return ps;
      }, keyHolderRace);
      lastRound.add(keyHolderRace.getKey().longValue());
    }

    // last round
    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL_INSERT_RACE, Statement.RETURN_GENERATED_KEYS);

      ps.setNull(1, Types.INTEGER);
      ps.setNull(2, Types.INTEGER);
      ps.setNull(3, Types.INTEGER);
      ps.setLong(4, id);
      ps.setLong(5, 3);
      ps.setLong(6, lastRound.get(0));
      ps.setLong(7, lastRound.get(1));

      return ps;
    }, keyHolderRace);
  }

  @Override
  public void updateRacesOfTournament(TournamentStandingsDto standingsDto) {
    LOG.trace("race persistence updating races based on tree({})", standingsDto);

    Collection<Long> ids = jdbcNamed.query(SQL_SELECT_RACE_ID_TOURNAMENT_WINNER, new MapSqlParameterSource("tournamentId", standingsDto.id()),
            (resultSet, rowNum) -> resultSet.getLong("id"));

    Long finalWinnerRace = ids.stream().findFirst().orElse(null);

    Race tournamentWinnerRace = getRaceById(finalWinnerRace);
    fillTreeBranch(standingsDto.tree(), tournamentWinnerRace);

  }

  @Override
  public Long evaluateTournamentScoreOnParticipant(TournamentDetailParticipantDto participant, Tournament tournament) {
    LOG.trace("race persistence evaluating tournament score on participant({}) regarding tournament({})", participant, tournament);
    return jdbcTemplate.queryForObject(
            SQL_HORSE_SCORE_ON_TOURNAMENT,
            Long.class,
            participant.horseId(),
            participant.horseId(),
            participant.horseId(),
            tournament.getId(),
            participant.horseId());
  }
  private void fillTreeBranch(TournamentStandingsTreeDto tree, Race race) {
    LOG.trace("race persistence filling tree branch({}) with race ({})", tree, race);
    if (tree.branches() == null) {
      return;
    }

    jdbcTemplate.update(SQL_UPDATE_RACE,
            tree.branches()[0].thisParticipant() != null ? tree.branches()[0].thisParticipant().horseId() : null,
            tree.branches()[1].thisParticipant() != null ? tree.branches()[1].thisParticipant().horseId() : null,
            tree.thisParticipant() != null ? tree.thisParticipant().horseId() : null,
            race.getTournamentId(),
            race.getRaceId());

    fillTreeBranch(tree.branches()[0], getRaceById(race.getPreviousRaceFirstHorse()));
    fillTreeBranch(tree.branches()[1], getRaceById(race.getPreviousRaceSecondHorse()));
  }

  private Race getRaceById(Long id) {
    LOG.trace("race persistence getting race by id({})", id);
    Collection<Race> races = jdbcTemplate.query(SQL_SELECT_RACE_BY_ID, this::mapRaceRow, id);

    return races.stream().findFirst().orElse(null);
  }

  private Race mapRaceRow(ResultSet resultSet, int rownum) throws SQLException {
    LOG.trace("race persistence mapping race({})", resultSet);
    return new Race()
            .setRaceId(resultSet.getLong("id"))
            .setFirstHorse(resultSet.getObject("first_horse", Long.class))
            .setSecondHorse(resultSet.getObject("second_horse", Long.class))
            .setWinner(resultSet.getObject("winner", Long.class))
            .setRound(resultSet.getLong("round"))
            .setTournamentId(resultSet.getLong("tournament_id"))
            .setPreviousRaceFirstHorse(resultSet.getObject("previous_race_first_horse", Long.class))
            .setPreviousRaceSecondHorse(resultSet.getObject("previous_race_second_horse", Long.class));
  }
}
