import {
  Component,
  EventEmitter,
  Output,
  AfterViewInit,
  ViewChild,
  ElementRef
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
  template: `
    <div #mapContainer class="h-80 rounded-lg"></div>
  `
})
export class LocationMapComponent implements AfterViewInit {

  @ViewChild('mapContainer', { static: true })
  mapContainer!: ElementRef<HTMLDivElement>;

  @Output()
  positionSelected = new EventEmitter<{ lat: number; lng: number }>();

  private map!: L.Map;
  private marker?: L.Marker;

  ngAfterViewInit(): void {

    this.map = L.map(this.mapContainer.nativeElement).setView(
      [48.8566, 2.3522],
      13
    );

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng;

      if (this.marker) {
        this.marker.setLatLng(e.latlng);
      } else {
        this.marker = L.marker(e.latlng, {
          icon: hexavoltIcon
        }).addTo(this.map);
      }

      this.positionSelected.emit({ lat, lng });
    });
  }
}
