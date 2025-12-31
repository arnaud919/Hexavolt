import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { City } from '../models/city';
import { CityService } from '../services/city';
import { RegisterRequest } from '../models/register-request';
import { catchError, debounceTime, distinctUntilChanged, filter, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-signin',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, RouterModule],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})
export class SigninComponent {
  readonly fb = inject(FormBuilder);
  readonly authService = inject(AuthService);
  readonly cityApi = inject(CityService);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    address: ['', Validators.required],
    postalCode: ['', Validators.required],
    phone: ['', Validators.required],
    birthdate: ['', Validators.required],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required],
    cityQuery: new FormControl('', Validators.required)
  });

  readonly suggestions = signal<readonly City[]>([]);
  readonly selectedCityId = signal<number | null>(null);
  readonly showDropdown = signal(false);

  constructor() {
    this.form.controls['cityQuery'].valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      filter((query: string | null) => (query?.length ?? 0) >= 2),
      switchMap(query => this.cityApi.searchCities(query!)),
      catchError(() => of([]))
    ).subscribe(cities => {
      this.suggestions.set(cities);
      this.showDropdown.set(true);
    });
  }

  selectCity(city: City) {
    this.form.controls['cityQuery'].setValue(city.name);
    this.selectedCityId.set(city.id);
    this.showDropdown.set(false);
  }

  onCityInputChange() {
    this.selectedCityId.set(null);
  }

  hideDropdownIfClickedOutside(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.autocomplete-wrapper')) {
      this.showDropdown.set(false);
    }
  }

  onSubmit() {
    if (this.form.invalid || !this.selectedCityId()) return;

    const value = this.form.value;

    const data = {
      email: value.email ?? '',
      firstName: value.firstName ?? '',
      lastName: value.lastName ?? '',
      address: value.address ?? '',
      postalCode: value.postalCode ?? '',
      phone: value.phone ?? '',
      birthdate: value.birthdate ?? '',
      password: value.password ?? '',
      cityId: this.selectedCityId()!
    };

    this.authService.register(data).subscribe({
      next: () => console.log('Inscription réussie'),
      error: err => console.error('Erreur inscription', err)
    });
  }

  ngOnInit() {
    document.addEventListener('click', this.hideDropdownIfClickedOutside.bind(this));
  }

  ngOnDestroy() {
    document.removeEventListener('click', this.hideDropdownIfClickedOutside.bind(this));
  }
}