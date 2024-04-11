import {Component, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {debounceTime, Subject} from "rxjs";
import {TournamentService} from "../../service/tournament.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-tournament',
  standalone: true,
  imports: [
    RouterLink,
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './tournament.component.html',
  styleUrl: './tournament.component.scss'
})
export class TournamentComponent implements OnInit {

  tournaments: TournamentListDto[] = [];
  bannerError: string | null = null;
  searchParams: TournamentSearchParams = {};
  searchStartDate: string | null = null;
  searchEndDate: string | null = null;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

  reloadTournaments() {
    if (this.searchStartDate == null || this.searchStartDate === "") {
      delete this.searchParams.startDate;
    } else {
      this.searchParams.startDate = new Date(this.searchStartDate);
    }

    if (this.searchEndDate == null || this.searchEndDate === "") {
      delete this.searchParams.endDate;
    } else {
      this.searchParams.endDate = new Date(this.searchEndDate);
    }

    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data;
          this.bannerError = null;

        },
        error: error => {
          if (error.status == 400) {
            this.notification.error("Please check your search parameters!");
          }
          else {
            this.bannerError = 'Could not fetch tournaments: ' + error.message;
            const errorMessage = error.status === 0
              ? 'Is the backend up?'
              : error.message.message;
            if (error.status == 0) {
              this.notification.error(errorMessage, 'Could Not Fetch Tournaments');
            }
            else {
              this.notification.error('Could Not Fetch Tournaments - check your search parameters!');
            }
          }
        }
      });
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }
}
