import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { WeeklyScheduleService } from '../services/weekly-schedule.service';
import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { TIME_SLOTS } from '../models/time-slots';
import { DEFAULT_WEEK_DAYS } from '../models/default-week-days';

@Component({
  selector: 'app-my-charging-station-availability',
  standalone: true,
  imports: [CommonModule, FormsModule, ProfileLayoutComponent],
  templateUrl: './my-charging-station-availability.component.html',
  styleUrl: './my-charging-station-availability.component.css'
})
export class MyChargingStationAvailabilityComponent implements OnInit {

  readonly timeSlots = TIME_SLOTS;
  weekDays = structuredClone(DEFAULT_WEEK_DAYS);
  stationId!: number;
  successMessage = '';
  errorMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly weeklyScheduleService: WeeklyScheduleService
  ) { }

  ngOnInit(): void {
    this.stationId = Number(this.route.snapshot.paramMap.get('id'));
  }

  saveWeeklySchedule(): void {
    this.successMessage = '';
    this.errorMessage = '';

    const invalidDay = this.weekDays.find(day =>
      day.enabled && day.startTime >= day.endTime
    );

    if (invalidDay) {
      this.errorMessage = `Pour ${invalidDay.label}, l'heure de début doit être avant l'heure de fin.`;
      return;
    }

    const payload = this.weekDays
      .filter(day => day.enabled)
      .map(day => ({
        dayOfWeekId: day.id,
        startTime: day.startTime,
        endTime: day.endTime
      }));

    console.log('Payload horaires envoyé :', payload);

    this.weeklyScheduleService
      .updateWeeklySchedule(this.stationId, payload)
      .subscribe({
        next: () => {
          this.successMessage = 'Les horaires ont bien été enregistrés.';
        },
        error: () => {
          this.errorMessage = "Une erreur est survenue lors de l'enregistrement des horaires.";
        }
      });
  }
}