
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ModifiedSchedule } from '../models/modified-schedule';

@Injectable({
  providedIn: 'root'
})
export class ModifiedScheduleService {
  private readonly apiUrl = '/api/stations';

  constructor(private readonly http: HttpClient) {}

  getAll(stationId: number): Observable<ModifiedSchedule[]> {
    return this.http.get<ModifiedSchedule[]>(
      `${this.apiUrl}/${stationId}/modified-schedules`,
      { withCredentials: true }
    );
  }

  save(stationId: number, schedule: ModifiedSchedule): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/${stationId}/modified-schedules`,
      schedule,
      { withCredentials: true }
    );
  }

  delete(stationId: number, date: string): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${stationId}/modified-schedules/${date}`,
      { withCredentials: true }
    );
  }
}

