import {Component, OnInit} from '@angular/core';
import {TournamentStandingsDto} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;
  generateConfirmationVisible = false;
  standingsForGenerate : TournamentStandingsDto | null = null;

  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  onCloseDialog(): void {
    this.generateConfirmationVisible = false;
    this.standingsForGenerate = null;
  }

  showGenerateConfirimation() : void {
    if (this.standings != undefined) {
      this.standingsForGenerate = this.standings;
    }
    this.generateConfirmationVisible = true;
  }

  public ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {

      const tournamentId = params.get('id');
      if(tournamentId !== null){
        const id = parseInt(tournamentId, 10);
        this.service.getStanding(id).subscribe(
          (tournamentStanding: TournamentStandingsDto) => {
          this.standings = tournamentStanding;
        },
          (error) => {
          if (error.status == 404) {
            this.notification.error("Failed to access unavailable tournament!");
            this.router.navigate(['/tournaments']);
          }

          if (error.status == 0) {
            this.notification.error("Failed to retrieve resource!");
            this.router.navigate(['/tournaments']);
          }
          });
        }
    });
  }

  public submit(form: NgForm) {
    if (form.valid && this.standings) {
      this.service.updateStandings(this.standings, this.standings.id).subscribe(
        (updatedStandings: TournamentStandingsDto) => {
          this.standings = updatedStandings;
          this.notification.success("Tournament standings updated successfully!");
        },
        (error) => {
          if (error.status == 0 || error.status == 404) {
            this.notification.error("Save matches could not be done!");
            this.router.navigate(['/tournaments']);
          }
          else {
            if (error.error.errors) {
              this.notification.error(error.error.message);
              this.notification.error(error.error.errors)
            } else {
              this.notification.error(error.error.message);
            }
          }
        }
      );
    } else {
      this.notification.warning("Please fill in all required fields.");
    }
  }

  public generateFirstRound() {
    this.generateConfirmationVisible = false;
    this.standingsForGenerate = null;
    if (!this.standings){
      this.notification.error("No standings for this tournament have been loaded!");
    }
    else {
      this.service.generateFirstMatches(this.standings.id).subscribe(
        (tournamentStanding: TournamentStandingsDto) => {
        this.standings = tournamentStanding;
        this.notification.warning("Please do note that this removes all the previous matches!");
        this.notification.success("Generating first round matches on " + this.standings.name + " was successful!");
      },
        (error) => {

          if (error.status == 404) {
            this.notification.error("Tournament " + this.standings?.name + " was not found!");
            this.router.navigate(['/tournaments']);
          }
          else {
            if (error.status == 0) {
              this.notification.error("Generate first round matches could not be done!");
              this.router.navigate(['/tournaments']);
            }
            else {
              if (error.error.errors) {
                this.notification.warning(error.error.message);
                this.notification.warning(error.error.errors);
              }
              else {
                this.notification.error(error.error.message);
              }
            }
          }
        });

    }
  }
}
