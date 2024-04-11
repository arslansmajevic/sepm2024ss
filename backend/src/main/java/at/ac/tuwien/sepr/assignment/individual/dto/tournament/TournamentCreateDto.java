package at.ac.tuwien.sepr.assignment.individual.dto.tournament;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;

import java.time.LocalDate;

public record TournamentCreateDto(
    String name,
    LocalDate startDate,
    LocalDate endDate,
    HorseSelectionDto[] participants
) {
}
