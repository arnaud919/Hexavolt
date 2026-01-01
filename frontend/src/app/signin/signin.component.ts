import { CommonModule } from '@angular/common';
import { Component, ElementRef, inject, OnInit, signal, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { City } from '../models/city';
import { CityService } from '../services/city';
import { RegisterRequest } from '../models/register-request';
import { catchError, debounceTime, distinctUntilChanged, filter, of, switchMap, tap } from 'rxjs';
import { HostListener } from '@angular/core';


@Component({
  selector: 'app-signin',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, RouterModule],
  templateUrl: './signin.component.html',
  styleUrl: './signin.component.css'
})

export class SigninComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly cityApi = inject(CityService);

  // Formulaire
  readonly form: FormGroup = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      phone: ['', Validators.required],
      birthdate: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    },
    { validators: this.passwordsMatchValidator }
  );

  // Champs d’autocomplétion
  readonly cityQuery = new FormControl('');
  readonly suggestions = signal<City[]>([]);
  readonly cityFieldFocused = signal(false);
  private selectedCityId: number | null = null;

  ngOnInit(): void {

    this.cityQuery.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      filter((q): q is string => typeof q === 'string' && q.length >= 2),
      tap(() => this.selectedCityId = null),
      switchMap(q => this.cityApi.searchCities(q).pipe(
        catchError(() => of([]))
      ))
    ).subscribe(results => {
      this.suggestions.set([...results]);
      this.cityFieldFocused.set(true);
    });
  }

  setCityFieldFocus(value: boolean): void {
    console.log('Dropdown visibility:', value);
    this.cityFieldFocused.set(value);
  }

  selectCity(city: City): void {
    this.cityQuery.setValue(city.name);
    this.selectedCityId = city.id;
    setTimeout(() => this.setCityFieldFocus(false), 0);
  }

  private passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent): void {
    console.log('Document clicked', event.target); // 👈 test ici

    const target = event.target as HTMLElement;
    const clickedInside = target.closest('.autocomplete-container');

    if (!clickedInside) {
      this.setCityFieldFocus(false);
    }
  }

  onSubmit(): void {
    if (this.form.invalid || this.selectedCityId === null) return;

    const value = this.form.value;

    const data: RegisterRequest = {
      email: value.email,
      firstName: value.firstName,
      lastName: value.lastName,
      address: value.address,
      postalCode: value.postalCode,
      phone: value.phone,
      birthdate: value.birthdate,
      password: value.password,
      cityId: this.selectedCityId
    };

    this.authService.register(data).subscribe({
      next: () => console.log('Inscription réussie'),
      error: err => console.error('Erreur :', err)
    });
  }
}