import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Power } from '../models/power';
import { PowerService } from '../services/power.service';
import { ChargingStationService } from '../services/charging-station.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-station-create',
  imports: [ReactiveFormsModule, CommonModule],
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
      hourlyRate: [null, [Validators.required, Validators.min(0.1)]],
      isCustom: [false],
      instruction: [''],
    });

    this.powerService.getAll()
      .subscribe(powers => this.powers = powers);
  }

  submit(): void {
    if (this.form.invalid) return;

    this.stationService.create({
      locationId: this.locationId,
      powerId: this.form.value.powerId,
      hourlyRate: this.form.value.hourlyRate,
      isCustom: this.form.value.isCustom,
      instruction: this.form.value.instruction || undefined
    }).subscribe(() => {
      this.router.navigate(['/locations', this.locationId, 'stations']);
    });
  }
}
