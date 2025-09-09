import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHealthRecord, NewHealthRecord } from '../health-record.model';

export type PartialUpdateHealthRecord = Partial<IHealthRecord> & Pick<IHealthRecord, 'id'>;

type RestOf<T extends IHealthRecord | NewHealthRecord> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestHealthRecord = RestOf<IHealthRecord>;

export type NewRestHealthRecord = RestOf<NewHealthRecord>;

export type PartialUpdateRestHealthRecord = RestOf<PartialUpdateHealthRecord>;

export type EntityResponseType = HttpResponse<IHealthRecord>;
export type EntityArrayResponseType = HttpResponse<IHealthRecord[]>;

@Injectable({ providedIn: 'root' })
export class HealthRecordService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/health-records');

  create(healthRecord: NewHealthRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(healthRecord);
    return this.http
      .post<RestHealthRecord>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(healthRecord: IHealthRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(healthRecord);
    return this.http
      .put<RestHealthRecord>(`${this.resourceUrl}/${this.getHealthRecordIdentifier(healthRecord)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(healthRecord: PartialUpdateHealthRecord): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(healthRecord);
    return this.http
      .patch<RestHealthRecord>(`${this.resourceUrl}/${this.getHealthRecordIdentifier(healthRecord)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestHealthRecord>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestHealthRecord[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHealthRecordIdentifier(healthRecord: Pick<IHealthRecord, 'id'>): number {
    return healthRecord.id;
  }

  compareHealthRecord(o1: Pick<IHealthRecord, 'id'> | null, o2: Pick<IHealthRecord, 'id'> | null): boolean {
    return o1 && o2 ? this.getHealthRecordIdentifier(o1) === this.getHealthRecordIdentifier(o2) : o1 === o2;
  }

  addHealthRecordToCollectionIfMissing<Type extends Pick<IHealthRecord, 'id'>>(
    healthRecordCollection: Type[],
    ...healthRecordsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const healthRecords: Type[] = healthRecordsToCheck.filter(isPresent);
    if (healthRecords.length > 0) {
      const healthRecordCollectionIdentifiers = healthRecordCollection.map(healthRecordItem =>
        this.getHealthRecordIdentifier(healthRecordItem),
      );
      const healthRecordsToAdd = healthRecords.filter(healthRecordItem => {
        const healthRecordIdentifier = this.getHealthRecordIdentifier(healthRecordItem);
        if (healthRecordCollectionIdentifiers.includes(healthRecordIdentifier)) {
          return false;
        }
        healthRecordCollectionIdentifiers.push(healthRecordIdentifier);
        return true;
      });
      return [...healthRecordsToAdd, ...healthRecordCollection];
    }
    return healthRecordCollection;
  }

  protected convertDateFromClient<T extends IHealthRecord | NewHealthRecord | PartialUpdateHealthRecord>(healthRecord: T): RestOf<T> {
    return {
      ...healthRecord,
      createdAt: healthRecord.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restHealthRecord: RestHealthRecord): IHealthRecord {
    return {
      ...restHealthRecord,
      createdAt: restHealthRecord.createdAt ? dayjs(restHealthRecord.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestHealthRecord>): HttpResponse<IHealthRecord> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestHealthRecord[]>): HttpResponse<IHealthRecord[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
