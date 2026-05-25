import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChargingStationDetailComponent } from './my-charging-station-detail.component';

describe('MyChargingStationDetailComponent', () => {
  let component: MyChargingStationDetailComponent;
  let fixture: ComponentFixture<MyChargingStationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyChargingStationDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyChargingStationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
