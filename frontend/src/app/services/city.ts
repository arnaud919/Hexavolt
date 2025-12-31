import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { City } from '../models/city';

@Injectable({ providedIn: 'root' })
export class CityService {
  private http = inject(HttpClient);
  private baseUrl = '/api/cities';

    searchCities(query: string): Observable<readonly City[]> {
    return this.http.get<readonly City[]>(`/api/cities/search?q=${encodeURIComponent(query)}`);
  }
}
