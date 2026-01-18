import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Power } from '../models/power';

@Injectable({
  providedIn: 'root'
})
export class PowerService {

  private readonly apiUrl = '/api/powers';

  constructor(private http: HttpClient) { }

  getAll() {
    return this.http.get<Power[]>(this.apiUrl);
  }
}
