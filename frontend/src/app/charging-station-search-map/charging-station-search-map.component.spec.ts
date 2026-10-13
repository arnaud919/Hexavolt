import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationSearchMapComponent } from './charging-station-search-map.component';

describe('ChargingStationSearchMapComponent', () => {
  let component: ChargingStationSearchMapComponent;
  let fixture: ComponentFixture<ChargingStationSearchMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStationSearchMapComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationSearchMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
