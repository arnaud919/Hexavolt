import { Injectable } from '@angular/core';
import { ChargingStation } from '../models/charging-station';
import { HttpClient } from '@angular/common/http';
import { ChargingStationDetail } from '../models/charging-station-detail';
import { ChargingStationEdit } from '../models/charging-station-edit';
import { ChargingStationUpdate } from '../models/charging-station-update';

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

  delete(id: number) {
    return this.http.delete<void>(
      `${this.apiUrl}/stations/${id}`,
      { withCredentials: true }
    );
  }

  getForEdit(id: number) {
    return this.http.get<ChargingStationEdit>(`/api/stations/${id}/edit`, {
      withCredentials: true,
    });
  }

  update(id: number, payload: ChargingStationUpdate) {
    return this.http.put<void>(`/api/stations/${id}`, payload, {
      withCredentials: true,
    });
  }

}
