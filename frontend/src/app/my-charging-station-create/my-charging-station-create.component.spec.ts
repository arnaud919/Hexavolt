import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChargingStationCreateComponent } from './my-charging-station-create.component';

describe('MyChargingStationCreateComponent', () => {
  let component: MyChargingStationCreateComponent;
  let fixture: ComponentFixture<MyChargingStationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyChargingStationCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyChargingStationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
