import { Routes } from '@angular/router';
import { IndexComponent } from './index/index.component';
import { ReserveStationComponent } from './reserve-station/reserve-station.component';
import { RentStationComponent } from './rent-station/rent-station.component';
import { LoginComponent } from './login/login.component';
import { SigninComponent } from './signin/signin.component';
import { LegalNoticeComponent } from './legal-notice/legal-notice.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { ContactComponent } from './contact/contact.component';
import { ModifyProfileComponent } from './modify-profile/modify-profile.component';
import { SignupConfirmationComponent } from './signup-confirmation/signup-confirmation.component';
import { VerifyPageComponent } from './verify-page/verify-page.component';
import { authGuard } from './services/auth.guard';
import { StationCreateComponent } from './station-create/station-create.component';
import { LocationStationsComponent } from './location-stations/location-stations.component';
import { LocationCreateComponent } from './location-create/location-create.component';
import { LocationListComponent } from './location-list/location-list.component';

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
        loadComponent: () => import('./profile/profile.component').then(m => m.ProfilComponent),
        canActivate: [authGuard]
    },
    { path: 'modify-profile', component: ModifyProfileComponent },
    { path: 'locations', component: LocationListComponent },
    { path: 'locations/:id/stations', component: LocationStationsComponent },
    { path: 'locations/:id/stations/new', component: StationCreateComponent },
    { path: 'locations/new', component: LocationCreateComponent }
];