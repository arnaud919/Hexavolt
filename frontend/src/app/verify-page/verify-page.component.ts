import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-verify-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './verify-page.component.html'
})
export class VerifyPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  loading = true;
  success: boolean | null = null;

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.success = false;
      this.loading = false;
      return;
    }

    this.http.get(`/api/auth/verify?token=${encodeURIComponent(token)}`).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
      },
      error: () => {
        this.success = false;
        this.loading = false;
      }
    });
  }
}
