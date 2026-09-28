import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';

import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { LocationService } from '../services/location.service';
import { CityService } from '../services/city';

import { City } from '../models/city';
import { ApiError } from '../models/api-error';
import { LocationUpdate } from '../models/location-update';

@Component({
  selector: 'app-my-location-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ProfileLayoutComponent],
  templateUrl: './my-location-edit.component.html',
})
export class MyLocationEditComponent implements OnInit {
  form!: FormGroup;

  citySearch = new FormControl('');

  locationId!: number;

  cities: readonly City[] = [];

  isLoading = true;
  citySearchError: string | null = null;
  submitError: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private locationService: LocationService,
    private cityService: CityService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nickname: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      cityId: [null, Validators.required],
    });

    this.locationId = Number(this.route.snapshot.paramMap.get('id'));

    this.listenCitySearch();
    this.loadLocation();
  }

  submit(): void {
    this.submitError = null;
    this.fieldErrors = {};

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: LocationUpdate = {
      nickname: this.form.value.nickname,
      address: this.form.value.address,
      postalCode: this.form.value.postalCode,
      cityId: this.form.value.cityId,
    };

    this.locationService.update(this.locationId, payload).subscribe({
      next: () => this.router.navigate(['/profil/lieux']),
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.submitError = apiError?.message ?? 'Impossible de modifier le lieu.';
        this.fieldErrors = apiError?.fieldErrors ?? {};
      },
    });
  }

  selectCity(city: City): void {
    this.form.patchValue({
      cityId: city.id,
    });

    this.citySearch.setValue(city.name, {
      emitEvent: false,
    });

    this.cities = [];
    this.citySearchError = null;

    delete this.fieldErrors['cityId'];
  }

  getFieldError(fieldName: string): string | null {
    return this.fieldErrors[fieldName] ?? null;
  }

  private loadLocation(): void {
    this.locationService.getMyLocationById(this.locationId).subscribe({
      next: location => {
        this.form.patchValue({
          nickname: location.nickname,
          address: location.address,
          postalCode: location.postalCode,
          cityId: location.cityId,
        });

        this.citySearch.setValue(location.cityName, {
          emitEvent: false,
        });

        this.isLoading = false;
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError | undefined;

        this.submitError = apiError?.message ?? 'Impossible de charger le lieu.';
        this.isLoading = false;
      },
    });
  }

  private listenCitySearch(): void {
    this.citySearch.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(value => {
          const search = value?.trim() ?? '';

          this.form.patchValue({
            cityId: null,
          });

          delete this.fieldErrors['cityId'];

          this.citySearchError = null;

          if (search.length < 2) {
            this.cities = [];
            return of<readonly City[]>([]);
          }

          return this.cityService.searchCities(search);
        })
      )
      .subscribe({
        next: cities => {
          this.cities = cities;

          if (cities.length === 0 && (this.citySearch.value?.trim().length ?? 0) >= 2) {
            this.citySearchError = 'Aucune ville trouvée.';
          }
        },
        error: () => {
          this.cities = [];
          this.citySearchError = 'Impossible de rechercher les villes.';
        },
      });
  }
}
