package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

import java.time.LocalDate;

public record TournamentListDto(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
