import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProfileUpdateRequest } from '../models/profile-update-request';
import { Profile } from '../models/profile';
import { ProfileDetails } from '../models/profile-details';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private readonly apiUrl = '/api/profile';

  constructor(private readonly http: HttpClient) { }

  /**
     * Récupère les informations complètes du profil
     * Utilisé pour la page "Modifier mon profil"
     */
  getProfileDetails(): Observable<ProfileDetails> {
    return this.http.get<ProfileDetails>(this.apiUrl, {
      withCredentials: true
    });
  }

  /**
   * Met à jour le profil utilisateur
   * Retourne un Profile "léger"
   */
  updateProfile(data: ProfileUpdateRequest): Observable<Profile> {
    return this.http.put<Profile>(this.apiUrl, data, {
      withCredentials: true
    });
  }
}
