import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AddressGeocodeResult {
  label: string;
  latitude: number;
  longitude: number;
  score: number;
}

export interface AddressReverseGeocodeResult {
  address: string;
  postalCode: string;
  cityName: string;
  cityCode: string;
  cityId: number | null;
  label: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  private readonly apiUrl = '/api/geocoding';

  constructor(private http: HttpClient) { }

  geocodeAddress(
    address: string,
    postalCode: string,
    cityId: number
  ): Observable<AddressGeocodeResult> {
    const params = new HttpParams()
      .set('address', address)
      .set('postalCode', postalCode)
      .set('cityId', cityId);

    return this.http.get<AddressGeocodeResult>(`${this.apiUrl}/address`, {
      params,
      withCredentials: true
    });
  }

  reverseGeocode(latitude: number, longitude: number): Observable<AddressReverseGeocodeResult> {
    const params = new HttpParams()
      .set('latitude', latitude)
      .set('longitude', longitude);

    return this.http.get<AddressReverseGeocodeResult>(`${this.apiUrl}/reverse`, {
      params,
      withCredentials: true
    });
  }
}
