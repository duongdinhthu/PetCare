import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IHealthRecord, NewHealthRecord } from '../health-record.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHealthRecord for edit and NewHealthRecordFormGroupInput for create.
 */
type HealthRecordFormGroupInput = IHealthRecord | PartialWithRequiredKeyOf<NewHealthRecord>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IHealthRecord | NewHealthRecord> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type HealthRecordFormRawValue = FormValueOf<IHealthRecord>;

type NewHealthRecordFormRawValue = FormValueOf<NewHealthRecord>;

type HealthRecordFormDefaults = Pick<NewHealthRecord, 'id' | 'createdAt'>;

type HealthRecordFormGroupContent = {
  id: FormControl<HealthRecordFormRawValue['id'] | NewHealthRecord['id']>;
  petId: FormControl<HealthRecordFormRawValue['petId']>;
  vetId: FormControl<HealthRecordFormRawValue['vetId']>;
  apptId: FormControl<HealthRecordFormRawValue['apptId']>;
  diagnosis: FormControl<HealthRecordFormRawValue['diagnosis']>;
  treatment: FormControl<HealthRecordFormRawValue['treatment']>;
  notes: FormControl<HealthRecordFormRawValue['notes']>;
  createdAt: FormControl<HealthRecordFormRawValue['createdAt']>;
};

export type HealthRecordFormGroup = FormGroup<HealthRecordFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HealthRecordFormService {
  createHealthRecordFormGroup(healthRecord: HealthRecordFormGroupInput = { id: null }): HealthRecordFormGroup {
    const healthRecordRawValue = this.convertHealthRecordToHealthRecordRawValue({
      ...this.getFormDefaults(),
      ...healthRecord,
    });
    return new FormGroup<HealthRecordFormGroupContent>({
      id: new FormControl(
        { value: healthRecordRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      petId: new FormControl(healthRecordRawValue.petId, {
        validators: [Validators.required],
      }),
      vetId: new FormControl(healthRecordRawValue.vetId, {
        validators: [Validators.required],
      }),
      apptId: new FormControl(healthRecordRawValue.apptId, {
        validators: [Validators.required],
      }),
      diagnosis: new FormControl(healthRecordRawValue.diagnosis),
      treatment: new FormControl(healthRecordRawValue.treatment),
      notes: new FormControl(healthRecordRawValue.notes),
      createdAt: new FormControl(healthRecordRawValue.createdAt),
    });
  }

  getHealthRecord(form: HealthRecordFormGroup): IHealthRecord | NewHealthRecord {
    return this.convertHealthRecordRawValueToHealthRecord(form.getRawValue() as HealthRecordFormRawValue | NewHealthRecordFormRawValue);
  }

  resetForm(form: HealthRecordFormGroup, healthRecord: HealthRecordFormGroupInput): void {
    const healthRecordRawValue = this.convertHealthRecordToHealthRecordRawValue({ ...this.getFormDefaults(), ...healthRecord });
    form.reset(
      {
        ...healthRecordRawValue,
        id: { value: healthRecordRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HealthRecordFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertHealthRecordRawValueToHealthRecord(
    rawHealthRecord: HealthRecordFormRawValue | NewHealthRecordFormRawValue,
  ): IHealthRecord | NewHealthRecord {
    return {
      ...rawHealthRecord,
      createdAt: dayjs(rawHealthRecord.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertHealthRecordToHealthRecordRawValue(
    healthRecord: IHealthRecord | (Partial<NewHealthRecord> & HealthRecordFormDefaults),
  ): HealthRecordFormRawValue | PartialWithRequiredKeyOf<NewHealthRecordFormRawValue> {
    return {
      ...healthRecord,
      createdAt: healthRecord.createdAt ? healthRecord.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
