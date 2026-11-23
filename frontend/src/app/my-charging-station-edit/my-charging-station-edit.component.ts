
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ChargingStationService } from '../services/charging-station.service';
import { PowerService } from '../services/power.service';
import { StatusChargingStationService } from '../services/status-charging-station.service.service';

import { LocationMapComponent } from '../location-map/location-map.component';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';

import { ApiError } from '../models/api-error';
import { ChargingStationUpdate } from '../models/charging-station-update';
import { Power } from '../models/power';
import { StatusChargingStation } from '../models/status-charging-station';

@Component({
  selector: 'app-my-charging-station-edit',
  standalone: true,
  imports: [
    ProfileLayoutComponent,
    ReactiveFormsModule,
    CommonModule,
    LocationMapComponent
  ],
  templateUrl: './my-charging-station-edit.component.html',
  styleUrl: './my-charging-station-edit.component.css'
})
export class MyChargingStationEditComponent implements OnInit {

  form!: FormGroup;

  stationId!: number;

  powers: Power[] = [];
  statuses: StatusChargingStation[] = [];

  isLoading = true;
  submitError: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private chargingStationService: ChargingStationService,
    private powerService: PowerService,
    private statusService: StatusChargingStationService
  ) { }

  ngOnInit(): void {
    this.stationId = Number(this.route.snapshot.paramMap.get('id'));

    this.form = this.fb.group({
      name: ['', Validators.required],
      powerId: [null, Validators.required],
      hourlyRate: [null, Validators.required],
      instruction: [''],
      isCustom: [false],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
      statusId: [null, Validators.required],
    });

    this.powerService.getAll().subscribe({
      next: powers => {
        this.powers = powers;
      },
      error: () => {
        this.submitError = 'Impossible de charger les puissances.';
      }
    });

    this.statusService.getAll().subscribe({
      next: statuses => {
        this.statuses = statuses.filter(
          status => status.name !== 'RETIRE'
        );
      },
      error: () => {
        this.submitError = 'Impossible de charger les statuts.';
      }
    });

    this.loadStation();
  }

  submit(): void {
    this.submitError = null;
    this.fieldErrors = {};

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: ChargingStationUpdate = {
      name: this.form.value.name,
      powerId: this.form.value.powerId,
      hourlyRate: this.form.value.hourlyRate,
      instruction: this.form.value.instruction,
      isCustom: this.form.value.isCustom,
      latitude: this.form.value.latitude,
      longitude: this.form.value.longitude,
      statusId: this.form.value.statusId,
    };

    this.chargingStationService.update(this.stationId, payload).subscribe({
      next: () => {
        this.router.navigate([
          '/profil',
          'bornes',
          this.stationId
        ]);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.submitError =
          apiError?.message ?? 'Impossible de modifier la borne.';

        this.fieldErrors = apiError?.fieldErrors ?? {};
      },
    });
  }

  onMapPositionSelected(position: { lat: number; lng: number }): void {
    this.form.patchValue({
      latitude: position.lat,
      longitude: position.lng,
      isCustom: true,
    });

    delete this.fieldErrors['latitude'];
    delete this.fieldErrors['longitude'];
  }

  getFieldError(fieldName: string): string | null {
    return this.fieldErrors[fieldName] ?? null;
  }

  private loadStation(): void {
    this.chargingStationService.getForEdit(this.stationId).subscribe({
      next: station => {
        this.form.patchValue({
          name: station.name,
          powerId: station.powerId,
          hourlyRate: station.hourlyRate,
          instruction: station.instruction,
          isCustom: station.isCustom,
          latitude: station.latitude,
          longitude: station.longitude,
          statusId: station.statusId,
        });

        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.submitError =
          apiError?.message ?? 'Impossible de charger la borne.';

        this.isLoading = false;
      },
    });
  }
}
