import {
  Component,
  EventEmitter,
  Output,
  AfterViewInit,
  ViewChild,
  ElementRef
} from '@angular/core';
import * as L from 'leaflet';

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
      [48.8566, 2.3522], // ✅ Paris
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
        this.marker = L.marker(e.latlng).addTo(this.map);
      }

      this.positionSelected.emit({ lat, lng });
    });
  }
}
