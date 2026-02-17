import { Routes } from '@angular/router';
import { IndexComponent } from './index/index.component';
import { ReserveStationComponent } from './reserve-station/reserve-station.component';
import { RentStationComponent } from './rent-station/rent-station.component';
import { LoginComponent } from './login/login.component';
import { SigninComponent } from './signin/signin.component';
import { LegalNoticeComponent } from './legal-notice/legal-notice.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { ContactComponent } from './contact/contact.component';
import { SignupConfirmationComponent } from './signup-confirmation/signup-confirmation.component';
import { VerifyPageComponent } from './verify-page/verify-page.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
    { path: '', component: IndexComponent },
    { path: 'reserver_une_borner', component: ReserveStationComponent },
    { path: 'louer_une_borne', component: RentStationComponent },
    { path: 'connexion', component: LoginComponent },
    { path: 'inscription', component: SigninComponent },
    { path: 'inscription/confirmation', component: SignupConfirmationComponent },
    { path: 'verify', component: VerifyPageComponent },
    { path: 'mentions_legales', component: LegalNoticeComponent },
    { path: 'politique_de_confidentialite', component: PrivacyPolicyComponent },
    { path: 'contact', component: ContactComponent },
    {
        path: 'profile',
        loadComponent: () => import('./profile/profile.component').then(m => m.ProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'profile/edit',
        loadComponent: () => import('./edit-profile/edit-profile.component').then(m => m.EditProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'profile/stations/:stationId/availability',
        loadComponent: () => import('./charging-station-availability/charging-station-availability.component').then(m => m.ChargingStationAvailability),
        canActivate: [authGuard]
    },
    {
        path: 'modify-profile',
        loadComponent: () => import('./modify-profile/modify-profile.component').then(m => m.ModifyProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'locations',
        loadComponent: () => import('./location-list/location-list.component').then(m => m.LocationListComponent),
        canActivate: [authGuard]
    },
    {
        path: 'locations/:id/stations',
        loadComponent: () => import('./location-stations/location-stations.component').then(m => m.LocationStationsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'locations/:id/stations/new',
        loadComponent: () => import('./station-create/station-create.component').then(m => m.StationCreateComponent),
        canActivate: [authGuard]
    },
    {
        path: 'locations/new',
        loadComponent: () => import('./location-create/location-create.component').then(m => m.LocationCreateComponent),
        canActivate: [authGuard]
    }
];