import { Routes } from '@angular/router';
import { IndexComponent } from './index/index.component';
import { ReserveStationComponent } from './reserve-station/reserve-station.component';
import { RentStationComponent } from './rent-station/rent-station.component';
import { LoginComponent } from './login/login.component';
import { SigninComponent } from './signin/signin.component';
import { LegalNoticeComponent } from './legal-notice/legal-notice.component';
import { PrivacyPolicyComponent } from './privacy-policy/privacy-policy.component';
import { ContactComponent } from './contact/contact.component';
import { ProfilComponent } from './profil/profil.component';
import { ModifyProfilComponent } from './modify-profil/modify-profil.component';
import { SignupConfirmationComponent } from './signup-confirmation/signup-confirmation.component';

export const routes: Routes = [
    { path: '', component: IndexComponent },
    { path: 'reserver_une_borner', component: ReserveStationComponent},
    { path: 'louer_une_borne', component: RentStationComponent},
    { path: 'connexion', component: LoginComponent},
    { path: 'inscription', component: SigninComponent},
    { path: 'inscription/confirmation', component: SignupConfirmationComponent },
    { path: 'mentions_legales', component: LegalNoticeComponent},
    { path: 'politique_de_confidentialite', component: PrivacyPolicyComponent},
    { path: 'contact', component: ContactComponent},
    { path: 'profil', component: ProfilComponent},
    { path: 'modify-profil', component: ModifyProfilComponent}
];