import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile-layout',
  imports: [RouterLink],
  templateUrl: './profile-layout.component.html',
  styleUrl: './profile-layout.component.css'
})
export class ProfileLayoutComponent {


  readonly backLabel = input('Retour à ma page de profil');

  readonly backLink = input<string | any[]>('/profil');

}
