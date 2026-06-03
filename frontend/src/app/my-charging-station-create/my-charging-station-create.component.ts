import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Power } from '../models/power';
import { PowerService } from '../services/power.service';
import { ChargingStationService } from '../services/charging-station.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LocationMapComponent } from "../location-map/location-map.component";

@Component({
  selector: 'app-station-create',
  imports: [ReactiveFormsModule, CommonModule, LocationMapComponent],
  templateUrl: './my-charging-station-create.component.html',
  styleUrl: './my-charging-station-create.component.css'
})
export class MyChargingStationCreateComponent implements OnInit {
  form!: FormGroup;
  powers: Power[] = [];
  locationId!: number;
  selectedPhoto: File | null = null;
  selectedVideo: File | null = null;

  constructor(
    private fb: FormBuilder,
    private powerService: PowerService,
    private stationService: ChargingStationService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.locationId = Number(this.route.snapshot.paramMap.get('id'));

    this.form = this.fb.group({
      powerId: [null, Validators.required],
      name: [''],
      hourlyRate: [null, [Validators.required, Validators.min(0.1)]],
      isCustom: [false],
      instruction: [''],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
    });

    this.powerService.getAll()
      .subscribe(powers => this.powers = powers);
  }

  onPositionSelected(pos: { lat: number; lng: number }) {
    this.form.patchValue({
      latitude: pos.lat,
      longitude: pos.lng
    });
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedPhoto = input.files?.[0] ?? null;
  }

  onVideoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedVideo = input.files?.[0] ?? null;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      locationId: this.locationId,
      name: this.form.value.name,
      powerId: Number(this.form.value.powerId),
      hourlyRate: Number(this.form.value.hourlyRate),
      instruction: this.form.value.instruction || undefined,
      isCustom: !!this.form.value.isCustom,
      latitude: this.form.value.latitude,
      longitude: this.form.value.longitude
    };

    const formData = new FormData();

    formData.append(
      'data',
      new Blob(
        [JSON.stringify(payload)],
        { type: 'application/json' }
      )
    );

    if (this.selectedPhoto) {
      formData.append('photo', this.selectedPhoto);
    }

    if (this.selectedVideo) {
      formData.append('video', this.selectedVideo);
    }

    this.stationService.create(formData).subscribe({
      next: () => {
        this.router.navigate([
          '/profil/lieux',
          this.locationId,
          'bornes'
        ]);
      },
      error: err => console.error('ERREUR BACKEND', err)
    });
  }
}
