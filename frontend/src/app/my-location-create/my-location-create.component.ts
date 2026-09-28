import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  debounceTime,
  distinctUntilChanged,
  of,
  switchMap
} from 'rxjs';

import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { LocationService } from '../services/location.service';
import { CityService } from '../services/city';
import { City } from '../models/city';
import { ApiError } from '../models/api-error';

@Component({
  selector: 'app-location-create',
  imports: [ReactiveFormsModule, CommonModule, ProfileLayoutComponent],
  templateUrl: './my-location-create.component.html',
  styleUrl: './my-location-create.component.css'
})
export class MyLocationCreateComponent implements OnInit {
  form!: FormGroup;

  citySearch = new FormControl('');
  cities: readonly City[] = [];

  citySearchError: string | null = null;
  submitError: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private locationService: LocationService,
    private cityService: CityService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      nickname: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      cityId: [null, Validators.required],
    });

    this.listenCitySearch();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitError = null;
    this.fieldErrors = {};

    this.locationService.create(this.form.value).subscribe({
      next: () => {
        this.router.navigate(['/profil/lieux']);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.submitError = apiError?.message ?? 'Impossible de créer le lieu.';
        this.fieldErrors = apiError?.fieldErrors ?? {};
      }
    });
  }

  selectCity(city: City): void {
    this.form.patchValue({
      cityId: city.id
    });

    this.citySearch.setValue(city.name, { emitEvent: false });
    this.cities = [];
    this.citySearchError = null;

    delete this.fieldErrors['cityId'];
  }

  getFieldError(fieldName: string): string | null {
    return this.fieldErrors[fieldName] ?? null;
  }

  private listenCitySearch(): void {
    this.citySearch.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        const search = value?.trim() ?? '';

        this.form.patchValue({
          cityId: null
        });

        this.citySearchError = null;
        delete this.fieldErrors['cityId'];

        if (search.length < 2) {
          this.cities = [];
          return of<readonly City[]>([]);
        }

        return this.cityService.searchCities(search);
      })
    ).subscribe({
      next: cities => {
        this.cities = cities;

        if (cities.length === 0 && (this.citySearch.value?.trim().length ?? 0) >= 2) {
          this.citySearchError = 'Aucune ville trouvée.';
        }
      },
      error: () => {
        this.cities = [];
        this.citySearchError = 'Impossible de rechercher les villes.';
      }
    });
  }
}