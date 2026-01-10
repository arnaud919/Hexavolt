import { CommonModule } from '@angular/common';
import { Component, ElementRef, HostListener, inject, ViewChild } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  @ViewChild('menuToggle') menuToggle!: ElementRef<HTMLInputElement>;
  @ViewChild('menuContainer') menuContainer!: ElementRef;

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly isLoggedIn = inject(AuthService).isLoggedIn;

  ngAfterViewInit(): void {
    document.addEventListener('click', this.handleClickOutside);
  }

  ngOnDestroy(): void {
    document.removeEventListener('click', this.handleClickOutside);
  }

  handleClickOutside = (event: MouseEvent): void => {
    const toggle = this.menuToggle?.nativeElement;
    const container = this.menuContainer?.nativeElement;

    if (
      toggle?.checked &&
      container &&
      !container.contains(event.target as Node)
    ) {
      toggle.checked = false;
    }
  };

  toggleMenu(menuToggle: HTMLInputElement) {
    menuToggle.checked = !menuToggle.checked;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/connexion']); // ou vers la page d’accueil selon ton choix
  }
}
