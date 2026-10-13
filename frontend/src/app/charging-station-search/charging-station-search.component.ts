import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ChargingStationSearch } from '../models/charging-station-search';
import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStationSearchMapComponent } from '../charging-station-search-map/charging-station-search-map.component';

@Component({
  selector: 'app-charging-station-search',
  standalone: true,
  imports: [CommonModule, RouterLink, ChargingStationSearchMapComponent],
  templateUrl: './charging-station-search.component.html',
  styleUrl: './charging-station-search.component.css'
})
export class ChargingStationSearchComponent implements OnInit {
  stations: readonly ChargingStationSearch[] = [];

  isLoading = true;
  errorMessage: string | null = null;
  selectedStation: ChargingStationSearch | null = null;

  constructor(private chargingStationService: ChargingStationService) { }

  ngOnInit(): void {
    this.loadStations();
  }

  private loadStations(): void {
    this.chargingStationService.searchActiveStations().subscribe({
      next: stations => {
        this.stations = stations;
        this.isLoading = false;
      },
      error: (_error: HttpErrorResponse) => {
        this.errorMessage = 'Impossible de charger les bornes disponibles.';
        this.isLoading = false;
      }
    });
  }

  selectStation(station: ChargingStationSearch): void {
    this.selectedStation = station;
  }
}
