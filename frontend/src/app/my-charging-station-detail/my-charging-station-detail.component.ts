
import { Component, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStationDetail } from '../models/charging-station-detail';
import { ApiError } from '../models/api-error';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { DEFAULT_WEEK_DAYS } from '../models/default-week-days';

@Component({
  selector: 'app-my-charging-station-detail',
  imports: [RouterLink, CommonModule, ProfileLayoutComponent],
  templateUrl: './my-charging-station-detail.component.html',
  styleUrl: './my-charging-station-detail.component.css'
})
export class MyChargingStationDetailComponent implements OnInit {

  readonly chargingStation = signal<ChargingStationDetail | null>(null);
  readonly loadError = signal<string | null>(null);

  readonly showWithdrawConfirmation = signal(false);
  readonly isWithdrawing = signal(false);
  readonly withdrawError = signal<string | null>(null);
  readonly withdrawSuccess = signal(false);

  readonly dayLabels = new Map(
    DEFAULT_WEEK_DAYS.map(day => [day.id, day.label])
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly chargingStationService: ChargingStationService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.chargingStationService.getMyChargingStationById(id).subscribe({
      next: station => {
        this.chargingStation.set(station);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.loadError.set(
          apiError?.message ?? 'Impossible de charger la borne.'
        );
      }
    });
  }

  openWithdrawConfirmation(): void {
    this.withdrawError.set(null);
    this.showWithdrawConfirmation.set(true);
  }

  cancelWithdraw(): void {
    this.showWithdrawConfirmation.set(false);
    this.withdrawError.set(null);
  }

  confirmWithdraw(): void {
    const station = this.chargingStation();

    if (!station || station.statusName === 'RETIRE' || this.isWithdrawing()) {
      return;
    }

    this.isWithdrawing.set(true);
    this.withdrawError.set(null);

    this.chargingStationService.withdrawMyChargingStation(station.id).subscribe({
      next: () => {
        this.chargingStation.update(current =>
          current ? { ...current, statusName: 'RETIRE' } : null
        );

        this.showWithdrawConfirmation.set(false);
        this.withdrawSuccess.set(true);
        this.isWithdrawing.set(false);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.withdrawError.set(
          apiError?.message ?? 'Impossible de retirer la borne.'
        );

        this.isWithdrawing.set(false);
      }
    });
  }
}
