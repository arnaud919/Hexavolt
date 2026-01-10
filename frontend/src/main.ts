import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { AuthService } from './app/services/auth.service';

bootstrapApplication(AppComponent, appConfig).then(appRef => {
  const authService = appRef.injector.get(AuthService);
  authService.checkAuth(); // ✅ Vérifie l'authentification au lancement
});