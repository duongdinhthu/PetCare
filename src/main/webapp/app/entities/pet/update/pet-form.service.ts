import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPet, NewPet } from '../pet.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPet for edit and NewPetFormGroupInput for create.
 */
type PetFormGroupInput = IPet | PartialWithRequiredKeyOf<NewPet>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPet | NewPet> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type PetFormRawValue = FormValueOf<IPet>;

type NewPetFormRawValue = FormValueOf<NewPet>;

type PetFormDefaults = Pick<NewPet, 'id' | 'createdAt'>;

type PetFormGroupContent = {
  id: FormControl<PetFormRawValue['id'] | NewPet['id']>;
  ownerId: FormControl<PetFormRawValue['ownerId']>;
  name: FormControl<PetFormRawValue['name']>;
  species: FormControl<PetFormRawValue['species']>;
  breed: FormControl<PetFormRawValue['breed']>;
  age: FormControl<PetFormRawValue['age']>;
  gender: FormControl<PetFormRawValue['gender']>;
  photoUrl: FormControl<PetFormRawValue['photoUrl']>;
  createdAt: FormControl<PetFormRawValue['createdAt']>;
};

export type PetFormGroup = FormGroup<PetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PetFormService {
  createPetFormGroup(pet: PetFormGroupInput = { id: null }): PetFormGroup {
    const petRawValue = this.convertPetToPetRawValue({
      ...this.getFormDefaults(),
      ...pet,
    });
    return new FormGroup<PetFormGroupContent>({
      id: new FormControl(
        { value: petRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      ownerId: new FormControl(petRawValue.ownerId, {
        validators: [Validators.required],
      }),
      name: new FormControl(petRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      species: new FormControl(petRawValue.species, {
        validators: [Validators.maxLength(50)],
      }),
      breed: new FormControl(petRawValue.breed, {
        validators: [Validators.maxLength(50)],
      }),
      age: new FormControl(petRawValue.age),
      gender: new FormControl(petRawValue.gender),
      photoUrl: new FormControl(petRawValue.photoUrl, {
        validators: [Validators.maxLength(255)],
      }),
      createdAt: new FormControl(petRawValue.createdAt),
    });
  }

  getPet(form: PetFormGroup): IPet | NewPet {
    return this.convertPetRawValueToPet(form.getRawValue() as PetFormRawValue | NewPetFormRawValue);
  }

  resetForm(form: PetFormGroup, pet: PetFormGroupInput): void {
    const petRawValue = this.convertPetToPetRawValue({ ...this.getFormDefaults(), ...pet });
    form.reset(
      {
        ...petRawValue,
        id: { value: petRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PetFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertPetRawValueToPet(rawPet: PetFormRawValue | NewPetFormRawValue): IPet | NewPet {
    return {
      ...rawPet,
      createdAt: dayjs(rawPet.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertPetToPetRawValue(
    pet: IPet | (Partial<NewPet> & PetFormDefaults),
  ): PetFormRawValue | PartialWithRequiredKeyOf<NewPetFormRawValue> {
    return {
      ...pet,
      createdAt: pet.createdAt ? pet.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
