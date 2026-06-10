import { Component, inject, OnInit, signal } from '@angular/core';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LocationService } from '../services/location.service';
import { LocationDetail } from '../models/location-detail';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-location-detail',
  imports: [ProfileLayoutComponent, CommonModule, RouterLink],
  templateUrl: './my-location-detail.component.html',
  styleUrl: './my-location-detail.component.css'
})
export class MyLocationDetailComponent implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly locationService = inject(LocationService);

  readonly location = signal<LocationDetail | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.locationService.getMyLocationById(id).subscribe({
      next: location => this.location.set(location),
      error: err => console.error('Erreur chargement lieu', err)
    });
  }
}
