import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-profile',
  imports: [RouterLink, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  readonly profile;

  private readonly http = inject(HttpClient);

  constructor(private authService: AuthService) {
    this.profile = this.authService.currentUser;
  }

  ngOnInit(): void {

    // 🧪 TEST A — appel direct
    this.http.get(
      '/api/locations/1/stations',
      { withCredentials: true }
    ).subscribe({
      next: res => console.log('TEST A OK', res),
      error: err => console.log('TEST A ERROR', err)
    });
  }
}

