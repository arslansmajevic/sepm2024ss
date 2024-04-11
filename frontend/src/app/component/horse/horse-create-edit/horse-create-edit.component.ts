import {Component, OnInit, ViewChild} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {Observable, of, retry} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {Breed} from "../../../dto/breed";
import {BreedService} from "../../../service/breed.service";
import {ConfirmDeleteDialogComponent} from "../../confirm-delete-dialog/confirm-delete-dialog.component";


export enum HorseCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: ConfirmDeleteDialogComponent;

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
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
  deleteConfirmationVisible = false;
  horseForDeletion: Horse | null = null;

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = value;
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
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit ' + this.horse.name;
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Save';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }


  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      default:
        return 'updated';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === HorseCreateEditMode.edit) {
      this.route.paramMap.subscribe((params: ParamMap) => {

        const horseId = params.get('id');
        if(horseId !== null){
          const id = parseInt(horseId, 10);
          this.service.getById(id).subscribe((horse: Horse) => {
            this.horse = horse;
            this.heightSet = true;
            this.weightSet = true;
            this.dateOfBirthSet = true;
          }, error => {
            // not availlable resource
            this.router.navigate(['/horses']); // Redirect in case of error
          })
        }
      });
    }

    if (this.mode === HorseCreateEditMode.create) {
      this.route.paramMap.subscribe((params: ParamMap) => {

        this.service.getById(1).subscribe((horse: Horse) => {
        }, error => {
          // not availlable resource
          if (error.status == 0) {
            this.router.navigate(['/horses']); // Redirect in case of error
          }
        })
      });
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    :  this.breedService.breedsByName(input, 5);

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode); // should never happen
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {

          if (error.status == 0) {
            this.notification.error("Can save the horse!");
            this.router.navigate(['/horses']);
          }
          else {
            if (error.status == 404) {
              this.notification.error("The horse " + this.horse.name + "was deleted!");
              this.router.navigate(['/horses']);
            }
            else {
              this.notification.error(error.error.errors)
              this.notification.error(error.error.message);
            }
          }
        }
      });
    }
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
            this.notification.error("Cannot send request!");
            this.notification.error("Is the backend up?");
          }
          else {
            if (error.status == 404) {
              this.notification.error("The horse " + this.horse.name + "was deleted or not present!");
              this.router.navigate(['/horses']);
            }
            else {
              this.notification.error(error.error.errors)
              this.notification.error(error.error.message);
            }
          }
        }
      );
    } else {
      // should not happen
      this.notification.error('The horse has not been loaded');
      this.router.navigate(['horses']);
    }
  }

}
