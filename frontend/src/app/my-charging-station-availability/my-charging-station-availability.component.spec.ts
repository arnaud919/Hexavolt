import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChargingStationAvailabilityComponent } from './my-charging-station-availability.component';

describe('MyChargingStationAvailabilityComponentComponent', () => {
  let component: MyChargingStationAvailabilityComponent;
  let fixture: ComponentFixture<MyChargingStationAvailabilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyChargingStationAvailabilityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyChargingStationAvailabilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
