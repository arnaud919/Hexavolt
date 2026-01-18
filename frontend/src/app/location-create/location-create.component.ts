import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LocationService } from '../services/location.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-location-create',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './location-create.component.html',
  styleUrl: './location-create.component.css'
})
export class LocationCreateComponent implements OnInit {
  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private locationService: LocationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      nickname: ['', Validators.required],
      address: ['', Validators.required],
      postalCode: ['', Validators.required],
      cityId: [null, Validators.required],
    });
  }

  submit(): void {
    if (this.form.invalid) return;

    this.locationService.create(this.form.value)
      .subscribe(() => {
        this.router.navigate(['/locations']);
      });
  }
}
