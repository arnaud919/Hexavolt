import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import * as L from 'leaflet';

import { ChargingStationSearch } from '../models/charging-station-search';

const hexavoltIcon = L.divIcon({
  className: '',
  html: `
    <div style="
      width: 26px;
      height: 26px;
      background: #f2c84b;
      border: 2px solid white;
      border-radius: 50% 50% 50% 0;
      transform: rotate(-45deg);
      box-shadow: 0 2px 6px rgba(0,0,0,.25);
      display: flex;
      align-items: center;
      justify-content: center;
    ">
      <div style="
        width: 10px;
        height: 10px;
        background: white;
        border-radius: 50%;
      "></div>
    </div>
  `,
  iconSize: [30, 30],
  iconAnchor: [15, 30],
  popupAnchor: [0, -30]
});

@Component({
  selector: 'app-charging-station-search-map',
  standalone: true,
  template: `
    <div #mapContainer class="h-[500px] rounded-2xl overflow-hidden border border-gray-300"></div>
  `
})
export class ChargingStationSearchMapComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('mapContainer', { static: true })
  mapContainer!: ElementRef<HTMLDivElement>;

  @Input()
  stations: readonly ChargingStationSearch[] = [];

  @Output()
  stationSelected = new EventEmitter<ChargingStationSearch>();

  private map?: L.Map;
  private markers: L.Marker[] = [];

  ngAfterViewInit(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView([46.603354, 1.888334], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);

    this.refreshMarkers();

    setTimeout(() => {
      this.map?.invalidateSize();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['stations']) {
      this.refreshMarkers();
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private refreshMarkers(): void {
    if (!this.map) {
      return;
    }

    this.markers.forEach(marker => marker.remove());
    this.markers = [];

    const validStations = this.stations
      .map(station => {
        const latitude = this.toCoordinate(station.latitude);
        const longitude = this.toCoordinate(station.longitude);

        return {
          ...station,
          latitude,
          longitude
        };
      })
      .filter(station =>
        Number.isFinite(station.latitude) &&
        Number.isFinite(station.longitude)
      );

    console.log('Stations reçues par la carte :', this.stations);
    console.log('Stations avec coordonnées valides :', validStations);

    validStations.forEach(station => {
      const marker = L.marker([station.latitude, station.longitude], {
        icon: hexavoltIcon
      }).addTo(this.map!);

      marker.bindPopup(`
      <strong>${station.name ?? 'Borne de recharge'}</strong><br>
      ${station.cityName}<br>
      ${station.hourlyRate} € / heure
    `);

      marker.on('click', () => {
        this.stationSelected.emit(station);
      });

      this.markers.push(marker);
    });

    if (validStations.length > 0) {
      const bounds = L.latLngBounds(
        validStations.map(station => [station.latitude, station.longitude])
      );

      this.map.fitBounds(bounds, {
        padding: [40, 40],
        maxZoom: 14
      });
    }

    console.table(this.stations);
  }

  private toCoordinate(value: unknown): number {
    if (typeof value === 'number') {
      return value;
    }

    if (typeof value === 'string') {
      return Number(value.replace(',', '.'));
    }

    return Number(value);
  }
}