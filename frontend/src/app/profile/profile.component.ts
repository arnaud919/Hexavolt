import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Profile } from '../models/profile';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [RouterLink, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfilComponent {
  profile?: Profile;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getProfile().subscribe(profile => {
      this.profile = profile;
    });
  }
}
