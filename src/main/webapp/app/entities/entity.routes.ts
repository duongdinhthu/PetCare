import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'petcareApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'pet',
    data: { pageTitle: 'petcareApp.pet.home.title' },
    loadChildren: () => import('./pet/pet.routes'),
  },
  {
    path: 'appointment',
    data: { pageTitle: 'petcareApp.appointment.home.title' },
    loadChildren: () => import('./appointment/appointment.routes'),
  },
  {
    path: 'health-record',
    data: { pageTitle: 'petcareApp.healthRecord.home.title' },
    loadChildren: () => import('./health-record/health-record.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
