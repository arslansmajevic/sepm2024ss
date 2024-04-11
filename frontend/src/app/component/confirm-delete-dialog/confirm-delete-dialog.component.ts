import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Horse } from '../../dto/horse';

@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
export class ConfirmDeleteDialogComponent {
  @Input() deleteWhat: Horse | null = null;
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
