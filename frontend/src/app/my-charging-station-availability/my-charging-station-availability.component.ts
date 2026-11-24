
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { ChargingStationService } from '../services/charging-station.service';
import { WeeklyScheduleService } from '../services/weekly-schedule.service';
import { ClosedDayService } from '../services/closed-day.service';
import { ModifiedScheduleService } from '../services/modified-schedule.service';

import { DEFAULT_WEEK_DAYS } from '../models/default-week-days';
import { TIME_SLOTS } from '../models/time-slots';
import { ClosedDay } from '../models/closed-day';
import { ModifiedSchedule } from '../models/modified-schedule';

@Component({
  selector: 'app-my-charging-station-availability',
  standalone: true,
  imports: [ReactiveFormsModule, ProfileLayoutComponent],
  templateUrl: './my-charging-station-availability.component.html',
  styleUrl: './my-charging-station-availability.component.css'
})
export class MyChargingStationAvailabilityComponent implements OnInit {

  readonly timeSlots = TIME_SLOTS;
  readonly weekDays = DEFAULT_WEEK_DAYS;
  readonly today = this.toDateInputValue(new Date());

  readonly weeklyForm: FormGroup;
  readonly closedDayForm: FormGroup;
  readonly modifiedScheduleForm: FormGroup;

  stationId = 0;
  pageError = '';

  closedDays: ClosedDay[] = [];
  modifiedSchedules: ModifiedSchedule[] = [];

  editingDate: string | null = null;

  isLoadingWeekly = true;
  isLoadingClosedDays = true;
  isLoadingModifiedSchedules = true;

  isSavingWeekly = false;
  isSavingClosedDay = false;
  isSavingModifiedSchedule = false;

  weeklyLoadError = '';
  closedDaysLoadError = '';
  modifiedSchedulesLoadError = '';

  weeklySuccess = '';
  weeklyError = '';

  closedDaySuccess = '';
  closedDayError = '';

  modifiedScheduleSuccess = '';
  modifiedScheduleError = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly chargingStationService: ChargingStationService,
    private readonly weeklyScheduleService: WeeklyScheduleService,
    private readonly closedDayService: ClosedDayService,
    private readonly modifiedScheduleService: ModifiedScheduleService
  ) {
    const dayGroups = this.weekDays.map(day => {
      const group = this.fb.group({
        enabled: [day.enabled],
        startTime: [{ value: day.startTime, disabled: !day.enabled }],
        endTime: [{ value: day.endTime, disabled: !day.enabled }]
      });

      group.controls.enabled.valueChanges.subscribe(enabled => {
        if (enabled) {
          group.controls.startTime.enable({ emitEvent: false });
          group.controls.endTime.enable({ emitEvent: false });
        } else {
          group.controls.startTime.disable({ emitEvent: false });
          group.controls.endTime.disable({ emitEvent: false });
        }
      });

      return group;
    });

    this.weeklyForm = this.fb.group({
      days: this.fb.array(dayGroups)
    });

    this.closedDayForm = this.fb.group({
      date: ['', Validators.required]
    });

    this.modifiedScheduleForm = this.fb.group({
      date: ['', Validators.required],
      startTime: ['08:00', Validators.required],
      endTime: ['09:00', Validators.required]
    });
  }

  get days(): FormArray {
    return this.weeklyForm.get('days') as FormArray;
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.pageError = 'Identifiant de borne invalide.';
      return;
    }

    this.stationId = id;

    this.loadWeeklySchedules();
    this.loadClosedDays();
    this.loadModifiedSchedules();
  }

  loadWeeklySchedules(): void {
    this.isLoadingWeekly = true;
    this.weeklyLoadError = '';

    this.chargingStationService
      .getMyChargingStationById(this.stationId)
      .subscribe({
        next: station => {
          const schedules = station.weeklySchedules ?? [];

          this.weekDays.forEach((day, index) => {
            const saved = schedules.find(
              schedule => Number(schedule.dayOfWeekId) === day.id
            );

            this.days.at(index).patchValue({
              enabled: !!saved,
              startTime: saved?.startTime?.slice(0, 5) ?? day.startTime,
              endTime: saved?.endTime?.slice(0, 5) ?? day.endTime
            });
          });

          this.isLoadingWeekly = false;
        },
        error: (error: HttpErrorResponse) => {
          this.weeklyLoadError = this.getErrorMessage(
            error, 'Impossible de charger les horaires hebdomadaires.'
          );
          this.isLoadingWeekly = false;
        }
      });
  }

  saveWeeklySchedule(): void {
    this.weeklySuccess = '';
    this.weeklyError = '';

    if (this.isLoadingWeekly || this.weeklyLoadError || this.isSavingWeekly) {
      return;
    }

    const invalidDay = this.weekDays.find((day, index) => {
      const value = this.days.at(index).getRawValue();
      return value.enabled && value.startTime >= value.endTime;
    });

    if (invalidDay) {
      this.weeklyError =
        `Pour ${invalidDay.label}, l'heure de début doit être avant l'heure de fin.`;
      return;
    }

    const payload = this.weekDays.flatMap((day, index) => {
      const value = this.days.at(index).getRawValue();

      return value.enabled ? [{
        dayOfWeekId: day.id,
        startTime: value.startTime,
        endTime: value.endTime
      }] : [];
    });

    this.isSavingWeekly = true;

    this.weeklyScheduleService
      .updateWeeklySchedule(this.stationId, payload)
      .subscribe({
        next: () => {
          this.isSavingWeekly = false;
          this.weeklySuccess = 'Les horaires hebdomadaires ont bien été enregistrés.';
          this.loadWeeklySchedules();
        },
        error: (error: HttpErrorResponse) => {
          this.isSavingWeekly = false;
          this.weeklyError = this.getErrorMessage(
            error, "Impossible d'enregistrer les horaires hebdomadaires."
          );
        }
      });
  }

  loadClosedDays(): void {
    this.isLoadingClosedDays = true;
    this.closedDaysLoadError = '';

    this.closedDayService.getAll(this.stationId).subscribe({
      next: days => {
        this.closedDays = days;
        this.isLoadingClosedDays = false;
      },
      error: (error: HttpErrorResponse) => {
        this.closedDaysLoadError = this.getErrorMessage(
          error, 'Impossible de charger les journées fermées.'
        );
        this.isLoadingClosedDays = false;
      }
    });
  }

  addClosedDay(): void {
    this.closedDaySuccess = '';
    this.closedDayError = '';

    if (this.closedDayForm.invalid || this.isSavingClosedDay) {
      this.closedDayForm.markAllAsTouched();
      return;
    }

    const date = this.closedDayForm.get('date')?.value as string;

    if (date < this.today) {
      this.closedDayError = 'La date ne peut pas être dans le passé.';
      return;
    }

    this.isSavingClosedDay = true;

    this.closedDayService.add(this.stationId, date).subscribe({
      next: () => {
        this.isSavingClosedDay = false;
        this.closedDaySuccess = 'La journée fermée a bien été ajoutée.';
        this.closedDayForm.reset();
        this.loadClosedDays();
      },
      error: (error: HttpErrorResponse) => {
        this.isSavingClosedDay = false;
        this.closedDayError = this.getErrorMessage(
          error, "Impossible d'ajouter cette journée fermée."
        );
      }
    });
  }

  deleteClosedDay(date: string): void {
    if (!window.confirm('Supprimer cette journée fermée ?')) {
      return;
    }

    this.closedDaySuccess = '';
    this.closedDayError = '';
    this.isSavingClosedDay = true;

    this.closedDayService.delete(this.stationId, date).subscribe({
      next: () => {
        this.isSavingClosedDay = false;
        this.closedDaySuccess = 'La journée fermée a bien été supprimée.';
        this.loadClosedDays();
      },
      error: (error: HttpErrorResponse) => {
        this.isSavingClosedDay = false;
        this.closedDayError = this.getErrorMessage(
          error, 'Impossible de supprimer cette journée fermée.'
        );
      }
    });
  }

  loadModifiedSchedules(): void {
    this.isLoadingModifiedSchedules = true;
    this.modifiedSchedulesLoadError = '';

    this.modifiedScheduleService.getAll(this.stationId).subscribe({
      next: schedules => {
        this.modifiedSchedules = schedules;
        this.isLoadingModifiedSchedules = false;
      },
      error: (error: HttpErrorResponse) => {
        this.modifiedSchedulesLoadError = this.getErrorMessage(
          error, 'Impossible de charger les horaires exceptionnels.'
        );
        this.isLoadingModifiedSchedules = false;
      }
    });
  }

  editModifiedSchedule(schedule: ModifiedSchedule): void {
    this.editingDate = schedule.date;
    this.modifiedScheduleSuccess = '';
    this.modifiedScheduleError = '';

    this.modifiedScheduleForm.patchValue({
      date: schedule.date,
      startTime: schedule.startTime.slice(0, 5),
      endTime: schedule.endTime.slice(0, 5)
    });
  }

  cancelEdit(): void {
    this.editingDate = null;

    this.modifiedScheduleForm.reset({
      date: '',
      startTime: '08:00',
      endTime: '09:00'
    });
  }

  saveModifiedSchedule(): void {
    this.modifiedScheduleSuccess = '';
    this.modifiedScheduleError = '';

    if (this.modifiedScheduleForm.invalid || this.isSavingModifiedSchedule) {
      this.modifiedScheduleForm.markAllAsTouched();
      return;
    }

    const value = this.modifiedScheduleForm.getRawValue();

    if (value.date < this.today) {
      this.modifiedScheduleError = 'La date ne peut pas être dans le passé.';
      return;
    }

    if (value.startTime >= value.endTime) {
      this.modifiedScheduleError =
        "L'heure de début doit être avant l'heure de fin.";
      return;
    }

    this.isSavingModifiedSchedule = true;

    this.modifiedScheduleService.save(this.stationId, {
      date: value.date,
      startTime: value.startTime,
      endTime: value.endTime
    }).subscribe({
      next: () => {
        this.isSavingModifiedSchedule = false;
        this.modifiedScheduleSuccess =
          "L'horaire exceptionnel a bien été enregistré.";
        this.cancelEdit();
        this.loadModifiedSchedules();
      },
      error: (error: HttpErrorResponse) => {
        this.isSavingModifiedSchedule = false;
        this.modifiedScheduleError = this.getErrorMessage(
          error, "Impossible d'enregistrer cet horaire exceptionnel."
        );
      }
    });
  }

  deleteModifiedSchedule(date: string): void {
    if (!window.confirm('Supprimer cet horaire exceptionnel ?')) {
      return;
    }

    this.modifiedScheduleSuccess = '';
    this.modifiedScheduleError = '';
    this.isSavingModifiedSchedule = true;

    this.modifiedScheduleService.delete(this.stationId, date).subscribe({
      next: () => {
        this.isSavingModifiedSchedule = false;
        this.modifiedScheduleSuccess =
          "L'horaire exceptionnel a bien été supprimé.";

        if (this.editingDate === date) {
          this.cancelEdit();
        }

        this.loadModifiedSchedules();
      },
      error: (error: HttpErrorResponse) => {
        this.isSavingModifiedSchedule = false;
        this.modifiedScheduleError = this.getErrorMessage(
          error, 'Impossible de supprimer cet horaire exceptionnel.'
        );
      }
    });
  }

  formatTime(time: string): string {
    return time.slice(0, 5);
  }

  formatDate(date: string): string {
    return new Date(`${date}T12:00:00`).toLocaleDateString('fr-FR', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  private toDateInputValue(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  private getErrorMessage(error: HttpErrorResponse, fallback: string): string {
    const message = error.error?.message;
    return typeof message === 'string' ? message : fallback;
  }
}
