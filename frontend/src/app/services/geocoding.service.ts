import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AddressGeocodeResult {
  label: string;
  latitude: number;
  longitude: number;
  score: number;
}

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  private readonly apiUrl = '/api/geocoding/address';

  constructor(private http: HttpClient) {}

  geocodeAddress(
    address: string,
    postalCode: string,
    cityId: number
  ): Observable<AddressGeocodeResult> {
    const params = new HttpParams()
      .set('address', address)
      .set('postalCode', postalCode)
      .set('cityId', cityId);

    return this.http.get<AddressGeocodeResult>(this.apiUrl, {
      params,
      withCredentials: true
    });
  }
}