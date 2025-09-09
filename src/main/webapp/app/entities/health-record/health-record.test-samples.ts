import dayjs from 'dayjs/esm';

import { IHealthRecord, NewHealthRecord } from './health-record.model';

export const sampleWithRequiredData: IHealthRecord = {
  id: 19677,
  petId: 32622,
  vetId: 14372,
  apptId: 10698,
};

export const sampleWithPartialData: IHealthRecord = {
  id: 2791,
  petId: 31953,
  vetId: 20837,
  apptId: 18203,
  diagnosis: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-09-09T09:30'),
};

export const sampleWithFullData: IHealthRecord = {
  id: 16942,
  petId: 3102,
  vetId: 5825,
  apptId: 6461,
  diagnosis: '../fake-data/blob/hipster.txt',
  treatment: '../fake-data/blob/hipster.txt',
  notes: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2025-09-09T10:33'),
};

export const sampleWithNewData: NewHealthRecord = {
  petId: 13969,
  vetId: 18788,
  apptId: 26123,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
