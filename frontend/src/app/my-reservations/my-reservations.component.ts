import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyReservation } from '../models/my-reservation';
import { ReservationService } from '../services/reservation.service';

@Component({
  selector: 'app-my-reservations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-reservations.component.html',
})
export class MyReservationsComponent implements OnInit {

  reservations: MyReservation[] = [];

  constructor(private reservationService: ReservationService) {}

  ngOnInit(): void {
    this.reservationService.getMyReservations()
      .subscribe(res => this.reservations = res);
  }
}

