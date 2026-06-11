import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() === true) {
    return true;
  }

  return authService.checkAuth().pipe(
    map(profile => profile ? true : router.createUrlTree(['/connexion']))
  );
};
