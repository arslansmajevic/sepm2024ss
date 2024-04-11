package at.ac.tuwien.sepr.assignment.individual.entity;

public class Participation {
  private Long id;
  private Long tournamentId;
  private Long horseId;
  private Integer entry;

  public Long getId() {
    return id;
  }

  public Participation setId(Long id) {
    this.id = id;
    return this;
  }

  public Long getTournamentId() {
    return tournamentId;
  }

  public Participation setTournamentId(Long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }

  public Long getHorseId() {
    return horseId;
  }

  public Participation setHorseId(Long horseId) {
    this.horseId = horseId;
    return this;
  }

  public Integer getEntry() {
    return entry;
  }

  public Participation setEntry(Integer entry) {
    this.entry = entry;
    return this;
  }

  @Override
  public String toString() {
    return "Participation{"
            + "id=" + id
            + ", tournamentId=" + tournamentId
            + ", horseId=" + horseId
            + ", entry=" + entry
            + '}';
  }
}
