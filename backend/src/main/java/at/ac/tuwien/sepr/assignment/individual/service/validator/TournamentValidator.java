package at.ac.tuwien.sepr.assignment.individual.service.validator;

import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participation;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final int NUMBER_OF_PARTICIPANTS = 8;
  private final HorseService horseService;

  public TournamentValidator(HorseService horseService) {
    this.horseService = horseService;
  }

  public void validateForCreate(TournamentCreateDto tournamentCreateDto) throws ValidationException {
    LOG.trace("validating tournament on create({})", tournamentCreateDto);
    List<String> validationErrors = new ArrayList<>();

    if (tournamentCreateDto.endDate() == null) {
      validationErrors.add("The tournament does not have an end date");
    } else {
      if (tournamentCreateDto.startDate() == null) {
        validationErrors.add("The tournament does not have a start date");
      } else {
        if (!tournamentCreateDto.startDate().isBefore(tournamentCreateDto.endDate())) {
          validationErrors.add("The start date is after the end date");
        }
      }
    }

    if (tournamentCreateDto.startDate() == null) {
      validationErrors.add("The tournament does not have a start date");
    }

    if (tournamentCreateDto.name() == null) {
      validationErrors.add("The name was not specified");
    } else {
      if (tournamentCreateDto.name().isEmpty()) {
        validationErrors.add("The name is empty");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }

    if (tournamentCreateDto.participants() != null) {
      if (tournamentCreateDto.participants().length != NUMBER_OF_PARTICIPANTS) {
        validationErrors.add("The tournament does not have 8 participants");
      }

      int counter = 1;
      for (HorseSelectionDto participantDto : tournamentCreateDto.participants()) {
        if (participantDto != null) {
          HorseDetailDto horse = null;
          try {
            horse = horseService.getById(participantDto.id());

            if (horse.dateOfBirth().isAfter(tournamentCreateDto.startDate())) {
              validationErrors.add("horse at position %d older than the start date".formatted(counter));
            }
          } catch (NotFoundException notFoundException) {
            validationErrors.add(String.format("The horse with id %d is not present in the database",
                    participantDto.id()));
          } catch (NullPointerException nullPointerException) {
            validationErrors.add(String.format("Horse at position %d with no id",
                    counter));
          }

        } else {
          validationErrors.add(String.format("Horse at position %d is not defined",
                  counter));
        }
        counter++;

      }
    } else {
      validationErrors.add("No participants were defined");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }

  public void validateUpdateStanding(long id, TournamentStandingsDto standingsDto) throws ValidationException {
    LOG.trace("validating tournament on update({})", standingsDto);
    List<String> validationErrors = new ArrayList<>();
    Set<Long> uniqueHorseIds = new HashSet<>();

    if (standingsDto.id() == null) {
      validationErrors.add("No id was defined in the standing");
    } else {
      if (id != standingsDto.id()) {
        validationErrors.add("Path id does not match up with standing id");
      }
    }
    var participants = standingsDto.participants();
    if (participants == null) {
      validationErrors.add("No participants in the standing");
    } else {
      if (participants.length != 8) {
        validationErrors.add("Number of participants is incorrect");
      }

      for (TournamentDetailParticipantDto participantDto : participants) {
        if (!uniqueHorseIds.add(participantDto.horseId())) {
          validationErrors.add("Duplicate horseId found: " + participantDto.horseId());
        }
      }
    }
    if (standingsDto.name() == null) {
      validationErrors.add("No name of the standingsDto was defined");
    }

    if (standingsDto.tree() == null) {
      validationErrors.add("No tree was defined");
    } else {
      validateTreeDepthHelper(standingsDto.tree(), 0, validationErrors);

      List<String> treeValidationErros =
              checkParticipantBranches(standingsDto.tree(), standingsDto.tree().thisParticipant(), uniqueHorseIds);

      validationErrors.addAll(treeValidationErros);
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Update standing failed on validation", validationErrors);
    }
  }

  private List<String> checkParticipantBranches(TournamentStandingsTreeDto tree,
                                      TournamentDetailParticipantDto winner,
                                      Set<Long> uniqueHorseIds) {

    List<String> validationErrors = new ArrayList<>();

    if (tree.branches() == null) {
      if (winner == null) {
        validationErrors.add("No participant defined as last in the tree");
      } else {
        if (!uniqueHorseIds.contains(winner.horseId())) {
          validationErrors.add("Horse with id %d does not belong in this tree".formatted(winner.horseId()));
        }
      }
    } else {
      if (tree.branches().length != 2) {
        validationErrors.add("Invalid number of branches on tree");
      }
      if (tree.branches()[0] == null && tree.branches()[1] == null) {
        validationErrors.add("Tree branch elements defined as null");
        return validationErrors;
      }
      if (tree.branches()[0] != null && tree.branches()[1] == null) {
        validationErrors.add("One tree branch element defined as null");
        return validationErrors;
      }
      if (tree.branches()[0] == null && tree.branches()[1] != null) {
        validationErrors.add("One tree branch element defined as null");
        return validationErrors;
      }

      if (tree.branches()[0].thisParticipant() != null && tree.branches()[1].thisParticipant() != null) {
        if (Objects.equals(tree.branches()[0].thisParticipant().horseId(), tree.branches()[1].thisParticipant().horseId())) {
          validationErrors.add("There are two horses with the same id playing a matches");
        }
      }

      if (winner == null) {
        validationErrors.addAll(
                checkParticipantBranches(tree.branches()[0], tree.branches()[0].thisParticipant(), uniqueHorseIds)
        );
        validationErrors.addAll(
                checkParticipantBranches(tree.branches()[1], tree.branches()[1].thisParticipant(), uniqueHorseIds));
      } else {
        if (winner.name() == null) {
          validationErrors.add("Horse name not defined");
        }

        if (winner.dateOfBirth() == null) {
          validationErrors.add("Horse date of birth not defined");
        }

        if (!uniqueHorseIds.contains(winner.horseId())) {
          validationErrors.add("Horse with name %s does not belong in this tree".formatted(tree.thisParticipant().name()));
        }

        if (tree.branches()[0].thisParticipant() == null || tree.branches()[1].thisParticipant() == null) {
          validationErrors.add("Horse " + winner.name() + " has no previous match defined");
          return validationErrors;
        }

        if (tree.branches()[0].thisParticipant().horseId() == null || tree.branches()[1].thisParticipant().horseId() == null) {
          validationErrors.add("Horses without ids found");
          return validationErrors;
        }

        if (!Objects.equals(winner.horseId(), tree.branches()[0].thisParticipant().horseId())
                && !Objects.equals(winner.horseId(), tree.branches()[1].thisParticipant().horseId())) {
          validationErrors.add("Horse with name %s is false appointed from two previous horses".formatted(winner.name()));
          return validationErrors;
        }

        validationErrors.addAll(
                checkParticipantBranches(tree.branches()[0], tree.branches()[0].thisParticipant(), uniqueHorseIds)
        );
        validationErrors.addAll(
                checkParticipantBranches(tree.branches()[1], tree.branches()[1].thisParticipant(), uniqueHorseIds));
      }
    }
    return validationErrors;
  }

  private void validateTreeDepthHelper(TournamentStandingsTreeDto tree, int depth, List<String> validationErrors) {
    if (tree == null) {
      return;
    }

    depth++;

    if (tree.branches() == null) {
      if (depth != 4) {
        validationErrors.add("Invalid depth in the tree. Expected depth: 4, Found depth: " + depth);
      }
      return;
    }

    validateTreeDepthHelper(tree.branches()[0], depth, validationErrors);
    validateTreeDepthHelper(tree.branches()[1], depth, validationErrors);
  }

  public void validateParticipations(TournamentDetailParticipantDto[] requestParticipants,
                                     Collection<Participation> databaseParticipations) throws ValidationException {
    List<String> validationErrors = new ArrayList<>();
    Set<Long> databaseHorseIds = extractHorseIds(databaseParticipations);
    Set<Long> missingHorseIds = new HashSet<>();

    for (TournamentDetailParticipantDto participant : requestParticipants) {
      Long horseId = participant.horseId();
      if (!databaseHorseIds.contains(horseId)) {
        missingHorseIds.add(horseId);
      }
    }

    if (!missingHorseIds.isEmpty()) {
      StringBuilder errorMessage = new StringBuilder("The following horse IDs are not present in the database participations for this tournament: ");
      for (Long missingHorseId : missingHorseIds) {
        errorMessage.append(missingHorseId).append(", ");
      }
      errorMessage.delete(errorMessage.length() - 2, errorMessage.length()); // Remove the last comma and space
      validationErrors.add(errorMessage.toString());
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of participations failed", validationErrors);
    }
  }

  private Set<Long> extractHorseIds(Collection<Participation> databaseParticipations) {
    Set<Long> horseIds = new HashSet<>();
    for (Participation participation : databaseParticipations) {
      horseIds.add(participation.getHorseId());
    }
    return horseIds;
  }

  public void validateDoubleParticipantsFromTree(TournamentStandingsTreeDto tree) throws ValidationException {
    List<TournamentDetailParticipantDto> list = new ArrayList<>();
    List<String> validationErrors = new ArrayList<>();
    list.addAll(iterateTreeForLastParticipant(tree));

    // Check for duplicates
    Set<TournamentDetailParticipantDto> set = new HashSet<>(list);
    if (set.size() < list.size()) {
      validationErrors.add("Duplicates found in the last nodes of the tree");
      throw new ValidationException("Validation of participations failed", validationErrors);
    }
  }

  private List<TournamentDetailParticipantDto> iterateTreeForLastParticipant(TournamentStandingsTreeDto tree) {
    if (tree.branches() == null) {
      List<TournamentDetailParticipantDto> list = new ArrayList<>();
      list.add(tree.thisParticipant());
      return list;
    } else {
      List<TournamentDetailParticipantDto> leftBranchParticipants = iterateTreeForLastParticipant(tree.branches()[0]);
      List<TournamentDetailParticipantDto> rightBranchParticipants = iterateTreeForLastParticipant(tree.branches()[1]);
      leftBranchParticipants.addAll(rightBranchParticipants);
      return leftBranchParticipants;
    }
  }
}
