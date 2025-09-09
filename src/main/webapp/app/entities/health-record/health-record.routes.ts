import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import HealthRecordResolve from './route/health-record-routing-resolve.service';

const healthRecordRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/health-record.component').then(m => m.HealthRecordComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/health-record-detail.component').then(m => m.HealthRecordDetailComponent),
    resolve: {
      healthRecord: HealthRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/health-record-update.component').then(m => m.HealthRecordUpdateComponent),
    resolve: {
      healthRecord: HealthRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/health-record-update.component').then(m => m.HealthRecordUpdateComponent),
    resolve: {
      healthRecord: HealthRecordResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default healthRecordRoute;
