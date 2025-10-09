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
  @ViewChild('menuContent') menuContent!: ElementRef;

  // Ferme le menu quand on clique ailleurs
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (
      this.menuToggle &&
      this.menuContent &&
      !this.menuToggle.nativeElement.contains(event.target as Node) &&
      !this.menuContent.nativeElement.contains(event.target as Node)
    ) {
      this.menuToggle.nativeElement.checked = false;
    }
  }

  // Ferme le menu manuellement (quand on clique sur un lien)
  closeMenu() {
    if (this.menuToggle) {
      this.menuToggle.nativeElement.checked = false;
    }
  }
}
