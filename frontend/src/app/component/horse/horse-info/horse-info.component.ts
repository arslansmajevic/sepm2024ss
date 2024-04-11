import {Component, OnInit} from '@angular/core';
import {Horse} from "../../../dto/horse";
import {Sex} from "../../../dto/sex";
import {HorseService} from "../../../service/horse.service";
import {BreedService} from "../../../service/breed.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Breed} from "../../../dto/breed";


@Component({
  selector: 'app-horse-info',
  templateUrl: './horse-info.component.html',
  styleUrl: './horse-info.component.scss'
})
export class HorseInfoComponent implements OnInit{

  deleteConfirmationVisible = false;
  horseForDeletion: Horse | null = null;
  horseId: number = 0;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: new Date(), // TODO this is bad
    height: 0, // TODO this is bad
    weight: 0, // TODO this is bad
  };

  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    return 'Info ' + this.horse.name;
  }

  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {

      const horseId = params.get('id');
      if(horseId !== null){
        const id = parseInt(horseId, 10);
        this.service.getById(id).subscribe((horse: Horse) => {
          this.horse = horse;
          this.heightSet = true;
          this.weightSet = true;
          this.dateOfBirthSet = true;
        },
          error => {
            this.router.navigate(['/horses']);
          })
      }
    });
  }


  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '\u200B';
  }

  showDeleteConfirmation(horse: Horse): void {
    this.horseForDeletion = horse;
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
          this.router.navigate(['/horses']);

        },
        error => {
          if (error.status == 0) {
            this.notification.error("Could not perform delete on horse!");
            this.router.navigate(['/horses']);
          }
          else {
            this.notification.error("The horse " + this.horse.name + " was deleted or not present!");
            this.router.navigate(['/horses']);
          }
        }
      );
    } else {
      this.notification.error('The horse has not been loaded');
      this.router.navigate(['/horses']);
    }
  }
}
