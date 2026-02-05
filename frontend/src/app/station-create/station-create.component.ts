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
  templateUrl: './station-create.component.html',
  styleUrl: './station-create.component.css'
})
export class StationCreateComponent implements OnInit {
  form!: FormGroup;
  powers: Power[] = [];
  locationId!: number;

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
    console.log('POSITION REÇUE', pos);
    this.form.patchValue({
      latitude: pos.lat,
      longitude: pos.lng
    });
  }

  submit(): void {
    console.log('FORM VALUE', this.form.value);

    const payload = {
      locationId: this.locationId,
      name: this.form.value.name,
      powerId: Number(this.form.value.powerId),
      hourlyRate: Number(this.form.value.hourlyRate),
      instruction: this.form.value.instruction || undefined,
      isCustom: !!this.form.value.isCustom,
      latitude: this.form.value.latitude,
      longitude: this.form.value.longitude,
    };

    this.stationService.create(payload).subscribe({
      next: () => {
        this.router.navigate(['/locations', this.locationId, 'stations']);
      },
      error: err => console.error('ERREUR BACKEND', err),
    });
  }
}
