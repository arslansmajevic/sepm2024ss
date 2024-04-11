package at.ac.tuwien.sepr.assignment.individual.entity;

public class Race {
  private Long raceId;
  private Long firstHorse;
  private Long secondHorse;
  private Long winner;
  private Long tournamentId;
  private Long round;
  private Long previousRaceFirstHorse;
  private Long previousRaceSecondHorse;

  public Long getPreviousRaceFirstHorse() {
    return previousRaceFirstHorse;
  }

  public Race setPreviousRaceFirstHorse(Long previousRaceFirstHorse) {
    this.previousRaceFirstHorse = previousRaceFirstHorse;
    return this;
  }

  public Long getPreviousRaceSecondHorse() {
    return previousRaceSecondHorse;
  }

  public Race setPreviousRaceSecondHorse(Long previousRaceSecondHorse) {
    this.previousRaceSecondHorse = previousRaceSecondHorse;
    return this;
  }

  public Long getRaceId() {
    return raceId;
  }

  public Race setRaceId(Long raceId) {
    this.raceId = raceId;
    return this;
  }

  public Long getFirstHorse() {
    return firstHorse;
  }

  public Race setFirstHorse(Long firstHorse) {
    this.firstHorse = firstHorse;
    return this;
  }

  public Long getSecondHorse() {
    return secondHorse;
  }

  public Race setSecondHorse(Long secondHorse) {
    this.secondHorse = secondHorse;
    return this;
  }

  public Long getWinner() {
    return winner;
  }

  public Race setWinner(Long winner) {
    this.winner = winner;
    return this;
  }

  public Long getTournamentId() {
    return tournamentId;
  }

  public Race setTournamentId(Long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }

  public Long getRound() {
    return round;
  }

  @Override
  public String toString() {
    return "Race{"
            + "raceId=" + raceId
            + ", firstHorse=" + firstHorse
            + ", secondHorse=" + secondHorse
            + ", winner=" + winner
            + ", tournamentId=" + tournamentId
            + ", round=" + round
            + ", previousRaceFirstHorse=" + previousRaceFirstHorse
            + ", previousRaceSecondHorse=" + previousRaceSecondHorse
            + '}';
  }

  public Race setRound(Long round) {
    this.round = round;
    return this;
  }

}
