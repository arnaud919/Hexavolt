import { Component } from '@angular/core';
import { ChargingStation } from '../models/charging-station';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChargingStationService } from '../services/charging-station.service';
import { CommonModule } from '@angular/common';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';

@Component({
  selector: 'app-location-stations',
  imports: [RouterLink, CommonModule, ProfileLayoutComponent],
  templateUrl: './my-location-stations.component.html',
  styleUrl: './my-location-stations.component.css'
})
export class MyLocationStationsComponent {
  stations: ChargingStation[] = [];
  locationId!: number;

  constructor(
    private route: ActivatedRoute,
    private stationService: ChargingStationService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      console.error('Location id missing in route');
      return;
    }

    this.locationId = Number(idParam);

    if (isNaN(this.locationId)) {
      console.error('Invalid location id:', idParam);
      return;
    }

    this.stationService.getByLocation(this.locationId).subscribe({
      next: (stations) => {
        this.stations = stations;
      },
      error: (err) => {
        console.error('Failed to load stations', err);
        this.stations = [];
      }
    });
  }
}

