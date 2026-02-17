import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../services/auth.service';
import { LocationService } from '../services/location.service';
import { ChargingStationService } from '../services/charging-station.service';

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

  readonly stationsPreview = signal<any[] | null>(null);

  ngOnInit(): void {
    this.loadLocations();
  }

  private loadLocations(): void {
    this.locationService.getMyLocations().subscribe({
      next: locations => {
        this.locations.set(locations);
        this.loadStationsPreview(locations);
      },
      error: err => {
        console.error('Erreur chargement lieux', err);
        this.locations.set([]);
        this.stationsPreview.set([]);
      }
    });
  }

  private loadStationsPreview(locations: any[]): void {
    if (!locations.length) {
      this.stationsPreview.set([]);
      return;
    }

    const firstLocation = locations[0];

    if (!firstLocation.locationId) {
      console.error('Location sans locationId', firstLocation);
      this.stationsPreview.set([]);
      return;
    }

    this.stationService.getByLocation(firstLocation.locationId).subscribe({
      next: stations => {
        this.stationsPreview.set(stations.slice(0, 3));
      },
      error: err => {
        console.error('Erreur chargement bornes', err);
        this.stationsPreview.set([]);
      }
    });
  }

}
