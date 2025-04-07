import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RentStationComponent } from './rent-station.component';

describe('RentStationComponent', () => {
  let component: RentStationComponent;
  let fixture: ComponentFixture<RentStationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RentStationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RentStationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
