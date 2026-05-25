import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../services/auth.service';
import { LocationService } from '../services/location.service';
import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStation } from '../models/charging-station';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  private readonly authService = inject(AuthService);
  private readonly locationService = inject(LocationService);
  private readonly stationService = inject(ChargingStationService);

  readonly profile = this.authService.currentUser;

  readonly locations = signal<any[] | null>(null);
  readonly stationsPreview = signal<ChargingStation[] | null>(null);

  ngOnInit(): void {
    this.loadLocations();
    this.loadStationsPreview();
  }

  private loadLocations(): void {
    this.locationService.getMyLocations().subscribe({
      next: locations => {
        this.locations.set(locations);
      },
      error: err => {
        console.error('Erreur chargement lieux', err);
        this.locations.set([]);
      }
    });
  }

private loadStationsPreview(): void {
  this.stationService.getMyStations().subscribe({
    next: chargingStations => {
      this.stationsPreview.set(chargingStations.slice(0, 3));
    },
    error: err => {
      this.stationsPreview.set([]);
    }
  });
}
}