import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReserveStationComponent } from './reserve-station.component';

describe('ReserveStationComponent', () => {
  let component: ReserveStationComponent;
  let fixture: ComponentFixture<ReserveStationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReserveStationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReserveStationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
