package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

public record TournamentStandingsTreeDto(
    TournamentDetailParticipantDto thisParticipant,
    TournamentStandingsTreeDto[] branches
) {

}
