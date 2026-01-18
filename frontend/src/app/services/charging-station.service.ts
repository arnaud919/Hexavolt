import { Injectable } from '@angular/core';
import { ChargingStation } from '../models/charging-station';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ChargingStationService {

  private readonly apiUrl = '/api';

  constructor(private http: HttpClient) { }

  getByLocation(locationId: number) {
    return this.http.get<ChargingStation[]>(
      `${this.apiUrl}/locations/${locationId}/stations`,
      { withCredentials: true }
    );
  }

  create(payload: {
    locationId: number;
    powerId: number;
    hourlyRate: number;
    instruction?: string;
    isCustom: boolean;
  }) {
    return this.http.post(
      `${this.apiUrl}/stations`,
      payload,
      { withCredentials: true }
    );
  }
}
