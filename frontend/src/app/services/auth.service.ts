import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { catchError, map, Observable, of, switchMap, tap } from 'rxjs';
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
  readonly isLoggedIn = signal<boolean | null>(null);
  readonly currentUser = signal<Profile | null>(null);

  constructor(private http: HttpClient) { }

  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(data: LoginRequest): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/login`, data, { withCredentials: true })
      .pipe(
        tap(() => {
          this.isLoggedIn.set(true);
        })
      );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe({
      next: () => this.isLoggedIn.set(false),
      error: () => this.isLoggedIn.set(false)
    });
  }

  checkAuth(): void {
    this.http
      .get<Profile>(`${this.apiUrl}/me`, { withCredentials: true })
      .pipe(
        tap(profile => {
          this.currentUser.set(profile);
          this.isLoggedIn.set(true);
        }),
        catchError(err => {
          if (err.status === 401) {
            this.currentUser.set(null);
            this.isLoggedIn.set(false);
            return of(null);
          }
          console.error(err);
          return of(null);
        })
      )
      .subscribe();
  }

  getProfile() {
    return this.http.get<Profile>(`${this.apiUrl}/me`, { withCredentials: true });
  }
  
}