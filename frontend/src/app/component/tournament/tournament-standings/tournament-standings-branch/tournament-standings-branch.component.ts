import {Component, Input} from '@angular/core';
import {TournamentDetailParticipantDto, TournamentStandingsTreeDto} from "../../../../dto/tournament";
import {of} from "rxjs";

enum TournamentBranchPosition {
  FINAL_WINNER,
  UPPER,
  LOWER,
}

@Component({
  selector: 'app-tournament-standings-branch',
  templateUrl: './tournament-standings-branch.component.html',
  styleUrls: ['./tournament-standings-branch.component.scss']
})
export class TournamentStandingsBranchComponent {
  protected readonly TournamentBranchPosition = TournamentBranchPosition;
  @Input() branchPosition = TournamentBranchPosition.FINAL_WINNER;
  @Input() treeBranch: TournamentStandingsTreeDto | undefined;
  @Input() allParticipants: TournamentDetailParticipantDto[] = [];

  get isUpperHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.UPPER;
  }

  ngOnInit() {
  }

  get isLowerHalf(): boolean {
    return this.branchPosition === TournamentBranchPosition.LOWER;
  }

  get isFinalWinner(): boolean {
    return this.branchPosition === TournamentBranchPosition.FINAL_WINNER;
  }

  suggestions = (input: string) => {
    // The candidates are either the participants of the previous round matches in this branch
    // or, if this is the first round, all participant horses
    const allCandidates =
      this.treeBranch?.branches?.map(b => b.thisParticipant)
      ?? this.allParticipants;
    const results = allCandidates
        .filter(x => !!x)
        .map(x => <TournamentDetailParticipantDto><unknown>x)
        .filter((x) =>
            x.name.toUpperCase().match(new RegExp(`.*${input.toUpperCase()}.*`)));
    return of(results);
  };

  public formatParticipant(participant: TournamentDetailParticipantDto | null): string {
    return participant
      ? `${participant.name} (${new Date(participant.dateOfBirth).toLocaleDateString()})`
      : "";
  }
  @Input() disabled: boolean = false;
  disabledChild: boolean = false;
  isWinnerSet(): boolean {

    if (this.treeBranch?.branches == null) {
      return false;
    }

    if (
      this.treeBranch?.branches?.[0].thisParticipant != null &&
      this.treeBranch?.branches?.[1].thisParticipant != null &&
      this.treeBranch.thisParticipant != null) {
      this.disabledChild = true;
      return false;
    }

    if (
      this.treeBranch?.branches?.[0].thisParticipant != null &&
      this.treeBranch?.branches?.[1].thisParticipant != null) {
      this.disabledChild = false;
      return false;
    }

    return true;
  }
}
