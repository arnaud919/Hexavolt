import { CommonModule } from '@angular/common';
import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile-layout',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './profile-layout.component.html',
  styleUrl: './profile-layout.component.css'
})
export class ProfileLayoutComponent {


  readonly backLabel = input('Retour à ma page de profil');

  readonly backLink = input<string | any[]>('/profil');

}
