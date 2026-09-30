import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChargingStationEditComponent } from './my-charging-station-edit.component';

describe('MyChargingStationEditComponent', () => {
  let component: MyChargingStationEditComponent;
  let fixture: ComponentFixture<MyChargingStationEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyChargingStationEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyChargingStationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
