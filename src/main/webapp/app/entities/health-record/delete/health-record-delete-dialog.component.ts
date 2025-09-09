import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IHealthRecord } from '../health-record.model';
import { HealthRecordService } from '../service/health-record.service';

@Component({
  templateUrl: './health-record-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HealthRecordDeleteDialogComponent {
  healthRecord?: IHealthRecord;

  protected healthRecordService = inject(HealthRecordService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.healthRecordService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
