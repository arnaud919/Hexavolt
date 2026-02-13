import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProfileService } from '../services/profile.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { City } from '../models/city';
import { CityService } from '../services/city';
import { debounceTime, distinctUntilChanged, Subject, switchMap } from 'rxjs';
import { ProfileDetails } from '../models/profile-details';
import { ProfileUpdateRequest } from '../models/profile-update-request';

@Component({
  selector: 'app-edit-profile',
  imports: [CommonModule,
    ReactiveFormsModule,
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css'
})
export class EditProfileComponent implements OnInit {
  // UI state
  isLoading = false;
  errorMessage = '';

  // affichage
  email = '';

  // autocomplete villes
  cities: readonly City[] = [];
  private citySearch$ = new Subject<string>();

  // formulaire (déclaré, pas initialisé ici)
  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly profileService: ProfileService,
    private readonly cityService: CityService,
    private readonly router: Router
  ) {

    // formulaire
    this.form = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: [''],
      address: [''],
      postalCode: [''],
      cityId: [null, Validators.required],
      birthdate: ['']
    })
  };

  ngOnInit(): void {

    /* ===============================
       Chargement du profil détaillé
       =============================== */
    this.profileService.getProfileDetails().subscribe({
      next: (profile: ProfileDetails) => {
        this.email = profile.email;

        this.form.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          phone: profile.phone,
          address: profile.address,
          postalCode: profile.postalCode,
          cityId: profile.cityId,
          birthdate: profile.birthdate
        });
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les informations du profil';
      }
    });

    /* ===============================
       Autocomplete villes
       =============================== */
    this.citySearch$
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => this.cityService.searchCities(query))
      )
      .subscribe(cities => {
        this.cities = cities;
      });
  }

  /* ===============================
     Gestion de l’autocomplete
     =============================== */
  onCityInput(event: Event): void {
    const input = event.target as HTMLInputElement | null;

    if (!input) {
      return;
    }

    const value = input.value;

    if (value.length < 2) {
      this.cities = [];
      return;
    }

    this.citySearch$.next(value);
  }


  selectCity(city: City): void {
    this.form.patchValue({ cityId: city.id });
    this.cities = [];
  }

  /* ===============================
     Soumission du formulaire
     =============================== */
  submit(): void {
    if (this.form.invalid || this.isLoading) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const payload: ProfileUpdateRequest = this.form.value as ProfileUpdateRequest;

    this.profileService.updateProfile(payload).subscribe({
      next: () => {
        this.router.navigate(['/profile']);
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la mise à jour du profil';
        this.isLoading = false;
      }
    });
  }

  /* ===============================
     Annulation
     =============================== */
  cancel(): void {
    this.router.navigate(['/profile']);
  }
}