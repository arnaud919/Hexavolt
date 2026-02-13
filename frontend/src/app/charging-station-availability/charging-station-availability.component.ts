import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { WeekDayAvailability } from '../models/week-day-availability';

@Component({
  selector: 'app-charging-station-availability',
  imports: [CommonModule, FormsModule],
  templateUrl: './charging-station-availability.component.html',
  styleUrl: './charging-station-availability.component.css'
})
export class ChargingStationAvailability {

  stationId!: number;

  weekDays: WeekDayAvailability[] = [
    { id: 1, label: 'Lundi',    enabled: false, startTime: '', endTime: '' },
    { id: 2, label: 'Mardi',    enabled: false, startTime: '', endTime: '' },
    { id: 3, label: 'Mercredi', enabled: false, startTime: '', endTime: '' },
    { id: 4, label: 'Jeudi',    enabled: false, startTime: '', endTime: '' },
    { id: 5, label: 'Vendredi', enabled: false, startTime: '', endTime: '' },
    { id: 6, label: 'Samedi',   enabled: false, startTime: '', endTime: '' },
    { id: 7, label: 'Dimanche', enabled: false, startTime: '', endTime: '' }
  ];

  constructor(route: ActivatedRoute) {
    this.stationId = Number(route.snapshot.paramMap.get('stationId'));
  }

  saveWeeklySchedule(): void {
    console.log('Horaires hebdomadaires', this.weekDays);
  }
}
