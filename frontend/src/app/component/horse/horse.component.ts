import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseListDto} from '../../dto/horse';
import {HorseSearch} from '../../dto/horse';
import {debounceTime, map, Observable, of, Subject} from 'rxjs';
import {BreedService} from "../../service/breed.service";

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: HorseListDto[] = [];
  bannerError: string | null = null;
  searchParams: HorseSearch = {};
  searchBornEarliest: string | null = null;
  searchBornLatest: string | null = null;
  horseForDeletion: Horse | null = null;
  searchChangedObservable = new Subject<void>();
  deleteConfirmationVisible = false;

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadHorses()});
  }

  reloadHorses() {
    if (this.searchBornEarliest == null || this.searchBornEarliest === "") {
      delete this.searchParams.bornEarliest;
    } else {
      this.searchParams.bornEarliest = new Date(this.searchBornEarliest);
    }
    if (this.searchBornLatest == null || this.searchBornLatest === "") {
      delete this.searchParams.bornLastest;
    } else {
      this.searchParams.bornLastest = new Date(this.searchBornLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
          this.bannerError = null;
        },
        error: error => {
          if (error.status == 400) {
            this.notification.error("Please check your search parameters!");
          }
          else {
            this.bannerError = 'Could not fetch horses: ' + error.message;
            const errorMessage = error.status === 0
              ? 'Is the backend up?'
              : error.message.message;
            this.notification.error(errorMessage, 'Could Not Fetch Horses');
          }
        }
      });
  }
  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  breedSuggestions = (input: string): Observable<string[]> =>
    this.breedService.breedsByName(input, 5)
      .pipe(map(bs =>
        bs.map(b => b.name)));

  formatBreedName = (name: string) => name; // It is already the breed name, we just have to give a function to the component

  showDeleteConfirmation(horseId: number): void {
    this.service.getById(horseId).subscribe((horse: Horse) => {
      this.horseForDeletion = horse;
    });
    this.deleteConfirmationVisible = true;
  }

  onCloseDialog(): void {
    this.deleteConfirmationVisible = false;
    this.horseForDeletion = null;
  }

  onDelete(horse: Horse): void {

    if (horse.id !== undefined) {
      this.service.delete(horse.id).subscribe(
        (response: Horse) => {
          this.notification.success("Horse '" + response.name + "' has been deleted!");

          this.reloadHorses();
        },
        error => {
          if (error.status === 422) {
            this.notification.error("The horse '" + horse.name + "' is present on a tournament");
          }
          else {
            if (error.status === 404) {
              this.notification.error("The horse is not present in the databse");
            }
            else
            {
              if (error.status === 0) {
                this.notification.error("Delete can not be done - is the backend up?");
              }
            }
          }
          this.reloadHorses();
        }
      );
    } else {
      this.notification.error('The horse has not been loaded');
      this.reloadHorses();
    }

    this.onCloseDialog();
  }
}
