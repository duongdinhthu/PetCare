import dayjs from 'dayjs/esm';
import { ApptStatus } from 'app/entities/enumerations/appt-status.model';

export interface IAppointment {
  id: number;
  petId?: number | null;
  ownerId?: number | null;
  vetId?: number | null;
  apptTime?: dayjs.Dayjs | null;
  status?: keyof typeof ApptStatus | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewAppointment = Omit<IAppointment, 'id'> & { id: null };
