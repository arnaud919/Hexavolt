import { Component, inject, OnInit, signal } from '@angular/core';
import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStation } from '../models/charging-station';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';

@Component({
  selector: 'app-my-charging-station-list',
  imports: [CommonModule, RouterLink, ProfileLayoutComponent],
  templateUrl: './my-charging-station-list.component.html',
  styleUrl: './my-charging-station-list.component.css'
})
export class MyChargingStationListComponent implements OnInit {

  private readonly chargingStationService =
    inject(ChargingStationService);

  readonly chargingStations =
    signal<ChargingStation[]>([]);

  ngOnInit(): void {
    this.chargingStationService.getMyStations().subscribe({
      next: chargingStations => {
        this.chargingStations.set(chargingStations);
      },
      error: err => {
        console.error(
          'Erreur chargement bornes',
          err
        );
      }
    });
  }
}
