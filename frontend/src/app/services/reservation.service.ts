import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MyReservation } from '../models/my-reservation';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly apiUrl = '/api/reservations';

  constructor(private http: HttpClient) { }

  getMyReservations(): Observable<MyReservation[]> {
    return this.http.get<MyReservation[]>(
      `${this.apiUrl}/me`,
      { withCredentials: true }
    );
  }
}
