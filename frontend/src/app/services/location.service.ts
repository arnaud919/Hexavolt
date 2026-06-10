import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LocationList } from '../models/location-list';
import { LocationCreate } from '../models/location-create';
import { LocationDetail } from '../models/location-detail';


@Injectable({
  providedIn: 'root'
})
export class LocationService {

  private readonly apiUrl = '/api/locations';

  constructor(private http: HttpClient) { }

  getMyLocations() {
    return this.http.get<LocationList[]>(this.apiUrl, {
      withCredentials: true
    });
  }

  create(payload: LocationCreate) {
    return this.http.post(this.apiUrl, payload, {
      withCredentials: true
    });
  }

  getMyLocationById(id: number) {
    return this.http.get<LocationDetail>(
      `/api/locations/${id}`,
      { withCredentials: true }
    );
  }

}
