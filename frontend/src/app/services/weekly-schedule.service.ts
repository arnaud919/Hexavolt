import { Injectable } from '@angular/core';
import { WeekDayAvailability } from '../models/week-day-availability';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WeeklyScheduleService {

  private readonly apiUrl = '/api/stations';

  constructor(private readonly http: HttpClient) { }

  updateWeeklySchedule(
    stationId: number,
    payload: {
      dayOfWeekId: number;
      startTime: string;
      endTime: string;
    }[]
  ) {
    return this.http.put<void>(
      `/api/stations/${stationId}/weekly-schedules`,
      payload,
      { withCredentials: true }
    );
  }
}
