package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

import java.time.LocalDate;

public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    TournamentDetailParticipantDto[] participants
) {
}
