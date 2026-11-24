
import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, distinctUntilChanged, of, switchMap } from 'rxjs';

import { ReservationService } from '../services/reservation.service';
import { AvailableSlot } from '../models/available-slot';

@Component({
  selector: 'app-rent-station',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './rent-station.component.html',
  styleUrl: './rent-station.component.css'
})
export class RentStationComponent implements OnInit {

  readonly today = this.formatLocalDate(new Date());

  readonly form;

  stationId = 0;
  availableSlots: AvailableSlot[] = [];

  isLoading = false;
  errorMessage = '';
  pageError = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly reservationService: ReservationService,
    private readonly destroyRef: DestroyRef
  ) {
    this.form = this.fb.nonNullable.group({
      date: ['']
    });
  }

  ngOnInit(): void {
    this.stationId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(this.stationId) || this.stationId <= 0) {
      this.pageError = 'Identifiant de borne invalide.';
      return;
    }

    this.form.controls.date.valueChanges
      .pipe(
        distinctUntilChanged(),
        switchMap(date => {
          this.availableSlots = [];
          this.errorMessage = '';

          if (!date) {
            this.isLoading = false;
            return of([]);
          }

          if (date < this.today) {
            this.isLoading = false;
            this.errorMessage = 'La date doit être dans le futur.';
            return of([]);
          }

          this.isLoading = true;

          return this.reservationService
            .getAvailableSlots(this.stationId, date)
            .pipe(
              catchError((error: HttpErrorResponse) => {
                this.errorMessage =
                  typeof error.error?.message === 'string'
                    ? error.error.message
                    : 'Impossible de récupérer les créneaux disponibles.';

                return of([]);
              })
            );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(slots => {
        this.availableSlots = slots;
        this.isLoading = false;
      });
  }

  formatTime(time: string): string {
    return time.slice(0, 5);
  }

  private formatLocalDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }
}
