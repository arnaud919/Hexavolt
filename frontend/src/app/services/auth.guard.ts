// auth.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isLoggedIn = authService.isLoggedIn();

  // ⏳ état encore inconnu → on laisse passer
  if (isLoggedIn === null) {
    return true;
  }

  // ✅ connecté
  if (isLoggedIn) {
    return true;
  }

  // ❌ non connecté
  router.navigate(['/login']);
  return false;
};

