import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthStateService } from '../services/auth-state.service';

@Component({
  selector: 'app-signup-confirmation',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './signup-confirmation.component.html',
  styleUrl: './signup-confirmation.component.css'
})
export class SignupConfirmationComponent {
  private readonly http = inject(HttpClient);
  private readonly authState = inject(AuthStateService);

  successMessage: string | null = null;
  errorMessage: string | null = null;
  isSending = false;

  resend(): void {
    const email = this.authState.getLastRegisteredEmail(); // récupéré depuis l'inscription
    if (!email) {
      this.errorMessage = 'Email inconnu. Veuillez vous réinscrire.';
      return;
    }

    this.isSending = true;
    this.http.post('/api/auth/verify/resend', { email }).subscribe({
      next: () => {
        this.successMessage = 'Email de confirmation renvoyé.';
        this.errorMessage = null;
        this.isSending = false;
      },
      error: () => {
        this.errorMessage = 'Impossible de renvoyer l’email pour le moment.';
        this.successMessage = null;
        this.isSending = false;
      }
    });
  }
}