import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { HealthRecordService } from '../service/health-record.service';
import { IHealthRecord } from '../health-record.model';
import { HealthRecordFormGroup, HealthRecordFormService } from './health-record-form.service';

@Component({
  selector: 'jhi-health-record-update',
  templateUrl: './health-record-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HealthRecordUpdateComponent implements OnInit {
  isSaving = false;
  healthRecord: IHealthRecord | null = null;

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected healthRecordService = inject(HealthRecordService);
  protected healthRecordFormService = inject(HealthRecordFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: HealthRecordFormGroup = this.healthRecordFormService.createHealthRecordFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ healthRecord }) => {
      this.healthRecord = healthRecord;
      if (healthRecord) {
        this.updateForm(healthRecord);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('petcareApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const healthRecord = this.healthRecordFormService.getHealthRecord(this.editForm);
    if (healthRecord.id !== null) {
      this.subscribeToSaveResponse(this.healthRecordService.update(healthRecord));
    } else {
      this.subscribeToSaveResponse(this.healthRecordService.create(healthRecord));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHealthRecord>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(healthRecord: IHealthRecord): void {
    this.healthRecord = healthRecord;
    this.healthRecordFormService.resetForm(this.editForm, healthRecord);
  }
}
