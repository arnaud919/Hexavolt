
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { MyReservation } from '../models/my-reservation';
import { AvailableSlot } from '../models/available-slot';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly apiUrl = '/api/reservations';

  constructor(private readonly http: HttpClient) {}

  getMyReservations(): Observable<MyReservation[]> {
    return this.http.get<MyReservation[]>(
      `${this.apiUrl}/me`,
      { withCredentials: true }
    );
  }

  getAvailableSlots(
    stationId: number,
    date: string
  ): Observable<AvailableSlot[]> {
    const params = new HttpParams().set('date', date);

    return this.http.get<AvailableSlot[]>(
      `/api/public/stations/${stationId}/availability`,
      { params }
    );
  }
}

