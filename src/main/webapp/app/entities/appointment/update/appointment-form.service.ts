import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAppointment, NewAppointment } from '../appointment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAppointment for edit and NewAppointmentFormGroupInput for create.
 */
type AppointmentFormGroupInput = IAppointment | PartialWithRequiredKeyOf<NewAppointment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAppointment | NewAppointment> = Omit<T, 'apptTime' | 'createdAt'> & {
  apptTime?: string | null;
  createdAt?: string | null;
};

type AppointmentFormRawValue = FormValueOf<IAppointment>;

type NewAppointmentFormRawValue = FormValueOf<NewAppointment>;

type AppointmentFormDefaults = Pick<NewAppointment, 'id' | 'apptTime' | 'createdAt'>;

type AppointmentFormGroupContent = {
  id: FormControl<AppointmentFormRawValue['id'] | NewAppointment['id']>;
  petId: FormControl<AppointmentFormRawValue['petId']>;
  ownerId: FormControl<AppointmentFormRawValue['ownerId']>;
  vetId: FormControl<AppointmentFormRawValue['vetId']>;
  apptTime: FormControl<AppointmentFormRawValue['apptTime']>;
  status: FormControl<AppointmentFormRawValue['status']>;
  createdAt: FormControl<AppointmentFormRawValue['createdAt']>;
};

export type AppointmentFormGroup = FormGroup<AppointmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AppointmentFormService {
  createAppointmentFormGroup(appointment: AppointmentFormGroupInput = { id: null }): AppointmentFormGroup {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({
      ...this.getFormDefaults(),
      ...appointment,
    });
    return new FormGroup<AppointmentFormGroupContent>({
      id: new FormControl(
        { value: appointmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      petId: new FormControl(appointmentRawValue.petId, {
        validators: [Validators.required],
      }),
      ownerId: new FormControl(appointmentRawValue.ownerId, {
        validators: [Validators.required],
      }),
      vetId: new FormControl(appointmentRawValue.vetId, {
        validators: [Validators.required],
      }),
      apptTime: new FormControl(appointmentRawValue.apptTime, {
        validators: [Validators.required],
      }),
      status: new FormControl(appointmentRawValue.status),
      createdAt: new FormControl(appointmentRawValue.createdAt),
    });
  }

  getAppointment(form: AppointmentFormGroup): IAppointment | NewAppointment {
    return this.convertAppointmentRawValueToAppointment(form.getRawValue() as AppointmentFormRawValue | NewAppointmentFormRawValue);
  }

  resetForm(form: AppointmentFormGroup, appointment: AppointmentFormGroupInput): void {
    const appointmentRawValue = this.convertAppointmentToAppointmentRawValue({ ...this.getFormDefaults(), ...appointment });
    form.reset(
      {
        ...appointmentRawValue,
        id: { value: appointmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AppointmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      apptTime: currentTime,
      createdAt: currentTime,
    };
  }

  private convertAppointmentRawValueToAppointment(
    rawAppointment: AppointmentFormRawValue | NewAppointmentFormRawValue,
  ): IAppointment | NewAppointment {
    return {
      ...rawAppointment,
      apptTime: dayjs(rawAppointment.apptTime, DATE_TIME_FORMAT),
      createdAt: dayjs(rawAppointment.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertAppointmentToAppointmentRawValue(
    appointment: IAppointment | (Partial<NewAppointment> & AppointmentFormDefaults),
  ): AppointmentFormRawValue | PartialWithRequiredKeyOf<NewAppointmentFormRawValue> {
    return {
      ...appointment,
      apptTime: appointment.apptTime ? appointment.apptTime.format(DATE_TIME_FORMAT) : undefined,
      createdAt: appointment.createdAt ? appointment.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
