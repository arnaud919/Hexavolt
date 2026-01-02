import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthStateService {
  private lastRegisteredEmail: string | null = null;

  setLastRegisteredEmail(email: string): void {
    this.lastRegisteredEmail = email;
  }

  getLastRegisteredEmail(): string | null {
    return this.lastRegisteredEmail;
  }
}

