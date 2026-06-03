import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { StatusChargingStation } from '../models/status-charging-station';

@Injectable({
  providedIn: 'root'
})
export class StatusChargingStationService {

  private readonly apiUrl = '/api/status-charging-stations';

  constructor(
    private readonly http: HttpClient
  ) {}

  getAll(): Observable<StatusChargingStation[]> {
    return this.http.get<StatusChargingStation[]>(
      this.apiUrl
    );
  }
}
