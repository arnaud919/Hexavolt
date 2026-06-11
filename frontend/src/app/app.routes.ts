import { Routes } from '@angular/router';

import { IndexComponent } from './index/index.component';
import { RentStationComponent } from './rent-station/rent-station.component';
import { LoginComponent } from './login/login.component';
import { SigninComponent } from './signin/signin.component';
import { LegalNoticeComponent } from './legal-notice/legal-notice.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { ContactComponent } from './contact/contact.component';
import { SignupConfirmationComponent } from './signup-confirmation/signup-confirmation.component';
import { VerifyPageComponent } from './verify-page/verify-page.component';
import { StationPublicDetailComponent } from './station-public-detail/station-public-detail.component';
import { FindStationComponent } from './find-station/find-station.component';

import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: '', component: IndexComponent },
  { path: 'louer_une_borne', component: RentStationComponent },
  { path: 'connexion', component: LoginComponent },
  { path: 'inscription', component: SigninComponent },
  { path: 'inscription/confirmation', component: SignupConfirmationComponent },
  { path: 'verify', component: VerifyPageComponent },
  { path: 'mentions_legales', component: LegalNoticeComponent },
  { path: 'politique_de_confidentialite', component: PrivacyPolicyComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'bornes/:id', component: StationPublicDetailComponent },
  { path: 'chercher-une-borne', component: FindStationComponent },

  {
    path: 'reservations/nouvelle/:id',
    loadComponent: () =>
      import('./rent-station/rent-station.component')
        .then(m => m.RentStationComponent),
    canActivate: [authGuard]
  },

  {
    path: 'profil',
    canActivateChild: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./profile/profile.component')
            .then(m => m.ProfileComponent)
      },
      {
        path: 'modification',
        loadComponent: () =>
          import('./edit-profile/edit-profile.component')
            .then(m => m.EditProfileComponent)
      },
      {
        path: 'bornes',
        loadComponent: () =>
          import('./my-charging-station-list/my-charging-station-list.component')
            .then(m => m.MyChargingStationListComponent)
      },
      {
        path: 'bornes/:id',
        loadComponent: () =>
          import('./my-charging-station-detail/my-charging-station-detail.component')
            .then(m => m.MyChargingStationDetailComponent)
      },
      {
        path: 'bornes/:id/disponibilites',
        loadComponent: () =>
          import('./my-charging-station-availability/my-charging-station-availability.component')
            .then(m => m.MyChargingStationAvailabilityComponent)
      },
      {
        path: 'lieux',
        loadComponent: () =>
          import('./my-location-list/my-location-list.component')
            .then(m => m.MyLocationListComponent)
      },
      {
        path: 'lieux/nouveau',
        loadComponent: () =>
          import('./my-location-create/my-location-create.component')
            .then(m => m.MyLocationCreateComponent)
      },
      {
        path: 'lieux/:id',
        loadComponent: () =>
          import('./my-location-detail/my-location-detail.component')
            .then(m => m.MyLocationDetailComponent)
      },
      {
        path: 'lieux/:id/bornes',
        loadComponent: () =>
          import('./my-location-stations/my-location-stations.component')
            .then(m => m.MyLocationStationsComponent)
      },
      {
        path: 'lieux/:id/bornes/nouvelle',
        loadComponent: () =>
          import('./my-charging-station-create/my-charging-station-create.component')
            .then(m => m.MyChargingStationCreateComponent)
      },
      {
        path: 'reservations',
        loadComponent: () =>
          import('./my-reservations/my-reservations.component')
            .then(m => m.MyReservationsComponent)
      },
      {
        path: 'reservations/:id',
        loadComponent: () =>
          import('./my-reservation-detail/my-reservation-detail.component')
            .then(m => m.MyReservationDetailComponent)
      }
    ]
  }
];