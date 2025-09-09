import dayjs from 'dayjs/esm';

import { IPet, NewPet } from './pet.model';

export const sampleWithRequiredData: IPet = {
  id: 19355,
  ownerId: 17275,
  name: 'kooky',
};

export const sampleWithPartialData: IPet = {
  id: 30986,
  ownerId: 373,
  name: 'meanwhile pfft',
  breed: 'unless colorful',
  age: 17674,
  gender: 'FEMALE',
};

export const sampleWithFullData: IPet = {
  id: 10888,
  ownerId: 16979,
  name: 'but',
  species: 'willing remand',
  breed: 'woot',
  age: 31058,
  gender: 'MALE',
  photoUrl: 'wash',
  createdAt: dayjs('2025-09-09T11:35'),
};

export const sampleWithNewData: NewPet = {
  ownerId: 14151,
  name: 'bare demob abandoned',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
