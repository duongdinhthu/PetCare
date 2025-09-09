import dayjs from 'dayjs/esm';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface IPet {
  id: number;
  ownerId?: number | null;
  name?: string | null;
  species?: string | null;
  breed?: string | null;
  age?: number | null;
  gender?: keyof typeof Gender | null;
  photoUrl?: string | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewPet = Omit<IPet, 'id'> & { id: null };
