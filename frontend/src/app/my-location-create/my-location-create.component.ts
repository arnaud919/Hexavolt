import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  Subject,
  catchError,
  debounceTime,
  distinctUntilChanged,
  map,
  of,
  switchMap,
  tap
} from 'rxjs';

import { ProfileLayoutComponent } from '../layout/profile-layout/profile-layout.component';
import { LocationService } from '../services/location.service';
import { CityService } from '../services/city';
import { City } from '../models/city';
import { ApiError } from '../models/api-error';
import { LocationCreate } from '../models/location-create';
import { LocationMapComponent } from '../location-map/location-map.component';
import { GeocodingService } from '../services/geocoding.service';

interface MapPosition {
  lat: number;
  lng: number;
}

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
  isSubmitting = false;

  citySearchError: string | null = null;
  submitError: string | null = null;
  fieldErrors: Record<string, string> = {};

  private readonly mapPositions$ = new Subject<MapPosition>();

  constructor(
    private fb: FormBuilder,
    private locationService: LocationService,
    private cityService: CityService,
    private router: Router,
    private geocodingService: GeocodingService,
    private destroyRef: DestroyRef
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      nickname: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      cityId: [null, Validators.required],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required]
    });

    this.listenCitySearch();
    this.listenMapPositions();
  }

  submit(): void {
    this.submitError = null;
    this.fieldErrors = {};

    if (this.isGeocoding || this.isSubmitting) {
      return;
    }

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
      longitude: this.form.value.longitude
    };

    this.isSubmitting = true;

    this.locationService.create(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.router.navigate(['/profil/lieux']);
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError | undefined;
          this.submitError = apiError?.message ?? 'Impossible de créer le lieu.';
          this.fieldErrors = apiError?.fieldErrors ?? {};
          this.isSubmitting = false;
        }
      });
  }

  onMapPositionSelected(position: MapPosition): void {
    this.mapPositions$.next(position);
  }

  selectCity(city: City): void {
    this.form.patchValue({ cityId: city.id });
    this.citySearch.setValue(city.name, { emitEvent: false });
    this.cities = [];
    this.citySearchError = null;
    delete this.fieldErrors['cityId'];
  }

  getFieldError(fieldName: string): string | null {
    return this.fieldErrors[fieldName] ?? null;
  }

  private listenMapPositions(): void {
    this.mapPositions$.pipe(
      tap(position => {
        // Les coordonnées sont celles du clic, pas celles de l'adresse trouvée.
        this.form.patchValue({
          latitude: position.lat,
          longitude: position.lng,
          address: '',
          postalCode: '',
          cityId: null
        });

        this.citySearch.setValue('');
        this.cities = [];
        this.citySearchError = null;
        this.geocodingError = null;
        this.submitError = null;
        this.fieldErrors = {};
        this.isGeocoding = true;
      }),
      switchMap(position => this.geocodingService.reverseGeocode(position.lat, position.lng).pipe(
        map(result => ({ result, error: null as string | null })),
        catchError((error: HttpErrorResponse) => of({
          result: null,
          error: (error.error as ApiError | undefined)?.message
            ?? 'Impossible de trouver une adresse à proximité de cette position.'
        }))
      )),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(({ result, error }) => {
      this.isGeocoding = false;

      if (!result) {
        this.geocodingError = error;
        return;
      }

      this.form.patchValue({
        address: result.address,
        postalCode: result.postalCode,
        cityId: result.cityId ?? null
      });

      if (result.cityId != null) {
        // Ne pas déclencher l'autocomplétion qui viderait cityId.
        this.citySearch.setValue(result.cityName, { emitEvent: false });
        this.cities = [];
      } else {
        // Commune non retrouvée par code INSEE : laisser la sélection manuelle.
        this.citySearch.setValue(result.cityName);
      }
    });
  }

  private listenCitySearch(): void {
    this.citySearch.valueChanges.pipe(
      tap(() => {
        // L'identifiant devient immédiatement invalide si le texte est modifié.
        this.form.patchValue({ cityId: null });
        this.cities = [];
        this.citySearchError = null;
        delete this.fieldErrors['cityId'];
      }),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(value => {
        const search = value?.trim() ?? '';

        if (search.length < 2) {
          return of({ search, cities: [] as readonly City[], failed: false });
        }

        return this.cityService.searchCities(search).pipe(
          map(cities => ({ search, cities, failed: false })),
          catchError(() => of({ search, cities: [] as readonly City[], failed: true }))
        );
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(({ search, cities, failed }) => {
      // Ignore les anciennes réponses quand l'utilisateur a changé la ville.
      if (search !== (this.citySearch.value?.trim() ?? '')
          || this.form.get('cityId')?.value != null) {
        return;
      }

      this.cities = cities;

      if (failed) {
        this.citySearchError = 'Impossible de rechercher les villes.';
      } else if (cities.length === 0 && search.length >= 2) {
        this.citySearchError = 'Aucune ville trouvée.';
      } else {
        this.citySearchError = null;
      }
    });
  }
}
