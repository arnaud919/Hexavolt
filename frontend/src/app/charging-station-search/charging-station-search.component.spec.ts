import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationSearchComponent } from './charging-station-search.component';

describe('ChargingStationSearchComponent', () => {
  let component: ChargingStationSearchComponent;
  let fixture: ComponentFixture<ChargingStationSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStationSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
