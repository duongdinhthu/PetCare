import dayjs from 'dayjs/esm';

export interface IHealthRecord {
  id: number;
  petId?: number | null;
  vetId?: number | null;
  apptId?: number | null;
  diagnosis?: string | null;
  treatment?: string | null;
  notes?: string | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewHealthRecord = Omit<IHealthRecord, 'id'> & { id: null };
