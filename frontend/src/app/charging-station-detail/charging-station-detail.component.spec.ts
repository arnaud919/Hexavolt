import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationDetailComponent } from './charging-station-detail.component';

describe('ChargingStationDetailComponent', () => {
  let component: ChargingStationDetailComponent;
  let fixture: ComponentFixture<ChargingStationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStationDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
