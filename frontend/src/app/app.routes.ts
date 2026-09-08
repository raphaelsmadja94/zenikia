import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/landing/landing.page').then((m) => m.LandingPage),
  },
  {
    path: 'upload',
    loadComponent: () => import('./pages/upload/upload.page').then((m) => m.UploadPage),
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile.page').then((m) => m.ProfilePage),
  },
  {
    path: 'interview/:id',
    loadComponent: () => import('./pages/interview/interview.page').then((m) => m.InterviewPage),
  },
  {
    path: 'report/:id',
    loadComponent: () => import('./pages/report/report.page').then((m) => m.ReportPage),
  },
  { path: '**', redirectTo: '' },
];
