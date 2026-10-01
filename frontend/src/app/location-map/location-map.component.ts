import {
  Component,
  EventEmitter,
  Output,
  AfterViewInit,
  ViewChild,
  ElementRef,
  Input,
  OnChanges,
  SimpleChanges,
  OnDestroy
} from '@angular/core';
import * as L from 'leaflet';

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
  selector: 'app-location-map',
  standalone: true,
  template: `
    <div #mapContainer class="h-80 rounded-lg"></div>
  `
})
export class LocationMapComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('mapContainer', { static: true })
  mapContainer!: ElementRef<HTMLDivElement>;

  @Input()
  latitude: number | null = null;

  @Input()
  longitude: number | null = null;

  @Output()
  positionSelected = new EventEmitter<{ lat: number; lng: number }>();

  private map?: L.Map;
  private marker?: L.Marker;

  ngAfterViewInit(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView(
      [48.8566, 2.3522],
      13
    );

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);

    this.showCurrentPosition();

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.setMarker(e.latlng);

      this.positionSelected.emit({
        lat: e.latlng.lat,
        lng: e.latlng.lng
      });
    });

    setTimeout(() => {
      this.map?.invalidateSize();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['latitude'] || changes['longitude']) {
      this.showCurrentPosition();
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private showCurrentPosition(): void {
    if (!this.map) {
      return;
    }

    if (this.latitude === null || this.longitude === null) {
      return;
    }

    const lat = Number(this.latitude);
    const lng = Number(this.longitude);

    if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
      return;
    }

    const latLng = L.latLng(lat, lng);

    this.setMarker(latLng);
    this.map.setView(latLng, 16);
  }

  private setMarker(latLng: L.LatLng): void {
    if (this.marker) {
      this.marker.setLatLng(latLng);
      return;
    }

    this.marker = L.marker(latLng, {
      icon: hexavoltIcon
    }).addTo(this.map!);
  }
}