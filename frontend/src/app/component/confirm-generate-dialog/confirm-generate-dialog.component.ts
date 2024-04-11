import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TournamentStandingsDto} from "../../dto/tournament";

@Component({
  selector: 'app-confirm-generate-dialog',
  templateUrl: './confirm-generate-dialog.component.html',
  styleUrl: './confirm-generate-dialog.component.scss'
})
export class ConfirmGenerateDialogComponent {
  @Input() generateWhat: TournamentStandingsDto | null = null;
  @Output() confirm = new EventEmitter<void>();
  @Output() close = new EventEmitter<void>();
  isModalOpen: boolean = false;

  constructor() {}

  closeDialog() {
    this.isModalOpen = false;
    this.close.emit();
  }

  confirmDelete() {
    this.confirm.emit();
  }
}
