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
import { LocationCreate } from '../models/location-create';
import { LocationMapComponent } from '../location-map/location-map.component';
import { GeocodingService } from '../services/geocoding.service';

@Component({
  selector: 'app-location-create',
  imports: [
    ReactiveFormsModule,
    CommonModule,
    ProfileLayoutComponent,
    LocationMapComponent
  ],
  templateUrl: './my-location-create.component.html',
  styleUrl: './my-location-create.component.css'
})
export class MyLocationCreateComponent implements OnInit {
  form!: FormGroup;

  citySearch = new FormControl('');
  cities: readonly City[] = [];

  geocodingError: string | null = null;
  isGeocoding = false;

  citySearchError: string | null = null;
  submitError: string | null = null;
  fieldErrors: Record<string, string> = {};

  constructor(
    private fb: FormBuilder,
    private locationService: LocationService,
    private cityService: CityService,
    private router: Router,
    private geocodingService: GeocodingService,
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      nickname: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      cityId: [null, Validators.required],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
    });

    this.listenCitySearch();
  }

  submit(): void {
    this.submitError = null;
    this.fieldErrors = {};

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: LocationCreate = {
      nickname: this.form.value.nickname,
      address: this.form.value.address,
      postalCode: this.form.value.postalCode,
      cityId: this.form.value.cityId,
      latitude: this.form.value.latitude,
      longitude: this.form.value.longitude,
    };

    this.locationService.create(payload).subscribe({
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

  onMapPositionSelected(position: { lat: number; lng: number }): void {
    this.form.patchValue({
      latitude: position.lat,
      longitude: position.lng,
    });

    delete this.fieldErrors['latitude'];
    delete this.fieldErrors['longitude'];
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

  positionAddressOnMap(): void {
    this.geocodingError = null;

    const address = this.form.value.address;
    const postalCode = this.form.value.postalCode;
    const cityId = this.form.value.cityId;

    if (!address || !postalCode || !cityId) {
      this.geocodingError = 'Renseignez une adresse, un code postal et une ville avant de positionner le lieu.';
      return;
    }

    this.isGeocoding = true;

    this.geocodingService.geocodeAddress(address, postalCode, cityId).subscribe({
      next: result => {
        this.form.patchValue({
          latitude: result.latitude,
          longitude: result.longitude
        });

        this.isGeocoding = false;
      },
      error: error => {
        this.geocodingError =
          error.error?.message ?? 'Impossible de positionner cette adresse.';

        this.isGeocoding = false;
      }
    });
  }
}