import { Component } from '@angular/core';
import { ChargingStation } from '../models/charging-station';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ChargingStationService } from '../services/charging-station.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-location-stations',
  imports: [RouterLink, CommonModule],
  templateUrl: './location-stations.component.html',
  styleUrl: './location-stations.component.css'
})
export class LocationStationsComponent {
  stations: ChargingStation[] = [];
  locationId!: number;

  constructor(
    private route: ActivatedRoute,
    private stationService: ChargingStationService
  ) { }

  ngOnInit(): void {
    this.locationId = Number(this.route.snapshot.paramMap.get('id'));

    this.stationService.getByLocation(this.locationId)
      .subscribe(stations => this.stations = stations);
  }
}
