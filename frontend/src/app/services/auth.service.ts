import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Profile } from '../models/profile';

interface RegisterRequest {
  firstName: string;
  lastName: string;
  address: string;
  postalCode: string;
  phone: string;
  birthdate: string;
  cityId: number;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})

export class AuthService {

  private readonly apiUrl = '/api/auth';
  readonly isLoggedIn = signal<boolean>(false);

  constructor(private http: HttpClient) { }

  // Inscription
  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  // Connexion
  login(data: LoginRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/login`, data, { withCredentials: true }).pipe(
      tap(() => this.isLoggedIn.set(true))
    );
  }

  // Déconnexion
  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.isLoggedIn.set(false),
      error: () => this.isLoggedIn.set(false) // même si erreur, on nettoie l'état local
    });
  }

  checkAuth(): void {
    this.http.get<{ email: string }>(`${this.apiUrl}/me`, { withCredentials: true }).subscribe({
      next: () => this.isLoggedIn.set(true),
      error: () => this.isLoggedIn.set(false)
    });
  }

  getProfile() {
    return this.http.get<Profile>(`${this.apiUrl}/me`, { withCredentials: true });
  }
}