import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  @ViewChild('menuToggle') menuToggle!: ElementRef<HTMLInputElement>;
  @ViewChild('menuContainer') menuContainer!: ElementRef;

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

}
