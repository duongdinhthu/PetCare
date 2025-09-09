import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHealthRecord } from '../health-record.model';
import { HealthRecordService } from '../service/health-record.service';

const healthRecordResolve = (route: ActivatedRouteSnapshot): Observable<null | IHealthRecord> => {
  const id = route.params.id;
  if (id) {
    return inject(HealthRecordService)
      .find(id)
      .pipe(
        mergeMap((healthRecord: HttpResponse<IHealthRecord>) => {
          if (healthRecord.body) {
            return of(healthRecord.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default healthRecordResolve;
