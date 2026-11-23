
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStationPublicDetail } from '../models/charging-station-public-detail';
import { DEFAULT_WEEK_DAYS } from '../models/default-week-days';

@Component({
  selector: 'app-station-public-detail',
  imports: [RouterLink],
  templateUrl: './station-public-detail.component.html',
  styleUrl: './station-public-detail.component.css'
})
export class StationPublicDetailComponent implements OnInit {

  readonly station = signal<ChargingStationPublicDetail | null>(null);
  readonly isLoading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  readonly dayLabels = new Map(
    DEFAULT_WEEK_DAYS.map(day => [day.id, day.label])
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly chargingStationService: ChargingStationService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage.set('Identifiant de borne invalide.');
      this.isLoading.set(false);
      return;
    }

    this.chargingStationService.getPublicChargingStationById(id).subscribe({
      next: station => {
        this.station.set(station);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage.set(
          error.status === 404
            ? 'Cette borne est introuvable ou indisponible.'
            : 'Impossible de charger les informations de cette borne.'
        );

        this.isLoading.set(false);
      }
    });
  }
}

