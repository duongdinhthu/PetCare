import dayjs from 'dayjs/esm';

import { IAppointment, NewAppointment } from './appointment.model';

export const sampleWithRequiredData: IAppointment = {
  id: 13399,
  petId: 19444,
  ownerId: 6552,
  vetId: 30447,
  apptTime: dayjs('2025-09-09T05:11'),
};

export const sampleWithPartialData: IAppointment = {
  id: 9734,
  petId: 18912,
  ownerId: 6232,
  vetId: 26200,
  apptTime: dayjs('2025-09-08T23:31'),
};

export const sampleWithFullData: IAppointment = {
  id: 5963,
  petId: 8956,
  ownerId: 31548,
  vetId: 19828,
  apptTime: dayjs('2025-09-09T12:07'),
  status: 'DONE',
  createdAt: dayjs('2025-09-09T00:18'),
};

export const sampleWithNewData: NewAppointment = {
  petId: 25778,
  ownerId: 24693,
  vetId: 18112,
  apptTime: dayjs('2025-09-09T06:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
