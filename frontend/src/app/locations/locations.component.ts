import { Component } from '@angular/core';
import { LocationList } from '../models/location-list';
import { LocationService } from '../services/location.service';

@Component({
  selector: 'app-locations',
  imports: [],
  templateUrl: './locations.component.html',
  styleUrl: './locations.component.css'
})
export class LocationsComponent {
  locations: LocationList[] = [];

  constructor(private locationService: LocationService) { }

  ngOnInit(): void {
    this.locationService.getMyLocations().subscribe(
      data => this.locations = data
    );
  }
}
