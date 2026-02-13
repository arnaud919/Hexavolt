import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationAvailabilityComponent } from './charging-station-availability.component';

describe('ChargingStationAvailabilityComponent', () => {
  let component: ChargingStationAvailabilityComponent;
  let fixture: ComponentFixture<ChargingStationAvailabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStationAvailabilityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationAvailabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
