import { Injectable } from '@angular/core';
import { ChargingStation } from '../models/charging-station';
import { HttpClient } from '@angular/common/http';
import { ChargingStationDetail } from '../models/charging-station-detail';

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

  getMyStations() {
    return this.http.get<ChargingStation[]>(
      `${this.apiUrl}/stations/me`,
      { withCredentials: true }
    );
  }

  create(formData: FormData) {
    return this.http.post(
      `${this.apiUrl}/stations`,
      formData,
      { withCredentials: true }
    );
  }

  getMyChargingStationById(id: number) {
    return this.http.get<ChargingStationDetail>(
      `${this.apiUrl}/stations/${id}`,
      { withCredentials: true }
    );
  }
  
}
