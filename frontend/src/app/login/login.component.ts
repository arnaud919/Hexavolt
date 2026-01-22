import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService, LoginRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);

  readonly loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  errorMessage = '';

  onSubmit(): void {
    console.log('SUBMIT TRIGGERED');

    if (this.loginForm.invalid) {
      console.log('FORM INVALID');
      return;
    }

    console.log('FORM VALID');

    const loginData: LoginRequest = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!
    };

    console.log('CALL LOGIN', loginData);

    this.authService.login(loginData).subscribe({
      next: () => {
        console.log('LOGIN SUCCESS');
        this.router.navigateByUrl('/profile');
      },
      error: err => {
        console.log('LOGIN ERROR', err);
        this.errorMessage = 'Échec de la connexion.';
      }
    });
  }
}