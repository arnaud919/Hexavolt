import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

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

  constructor(private http: HttpClient) {}
  
  private apiUrl = '/api/auth';

  // Inscription
  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  // Connexion
  login(data: LoginRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data).pipe(
      tap((res: any) => {
        // Exemple : stocker le token dans localStorage
        if (res && res.token) {
          localStorage.setItem('access_token', res.token);
        }
      })
    );
  }

  // Récupérer le token
  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  // Déconnexion
  logout(): void {
    localStorage.removeItem('access_token');
  }

  // Vérifier si connecté
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}