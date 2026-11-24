
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ClosedDay } from '../models/closed-day';

@Injectable({
  providedIn: 'root'
})
export class ClosedDayService {
  private readonly apiUrl = '/api/stations';

  constructor(private readonly http: HttpClient) {}

  getAll(stationId: number): Observable<ClosedDay[]> {
    return this.http.get<ClosedDay[]>(
      `${this.apiUrl}/${stationId}/closed-days`,
      { withCredentials: true }
    );
  }

  add(stationId: number, date: string): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${stationId}/closed-days`,
      { date },
      { withCredentials: true }
    );
  }

  delete(stationId: number, date: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${stationId}/closed-days/${date}`,
      { withCredentials: true }
    );
  }
}
