import { Component } from '@angular/core';
import { LocationList } from '../models/location-list';
import { LocationService } from '../services/location.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-location-list',
  imports: [CommonModule,RouterModule],
  templateUrl: './my-location-list.component.html',
  styleUrl: './my-location-list.component.css'
})
export class MyLocationListComponent {
locations: LocationList[] = [];

  constructor(private locationService: LocationService) {}

  ngOnInit(): void {
    this.locationService.getMyLocations()
      .subscribe(locations => this.locations = locations);
  }
}
