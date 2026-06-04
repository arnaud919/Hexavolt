import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChargingStationService } from '../services/charging-station.service';
import { ChargingStationDetail } from '../models/charging-station-detail';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-charging-station-detail',
  imports: [RouterLink, CommonModule],
  templateUrl: './my-charging-station-detail.component.html',
  styleUrl: './my-charging-station-detail.component.css'
})
export class MyChargingStationDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly chargingStationService = inject(ChargingStationService);

  readonly chargingStation = signal<ChargingStationDetail | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.chargingStationService.getMyChargingStationById(id).subscribe({
      next: chargingStation => {
        console.log('Détail borne :', chargingStation);
        this.chargingStation.set(chargingStation)
      },
      error: err => console.error('Erreur chargement borne', err)
    });
  }
}
