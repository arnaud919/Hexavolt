import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationStationsComponent } from './location-stations.component';

describe('LocationStationsComponent', () => {
  let component: LocationStationsComponent;
  let fixture: ComponentFixture<LocationStationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationStationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LocationStationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
