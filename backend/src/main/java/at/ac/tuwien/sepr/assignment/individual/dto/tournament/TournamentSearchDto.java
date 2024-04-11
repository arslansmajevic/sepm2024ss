package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TournamentSearchDto(
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        Integer limit
) {
}
