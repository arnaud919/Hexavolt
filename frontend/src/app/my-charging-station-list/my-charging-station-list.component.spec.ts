import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyChargingStationListComponent } from './my-charging-station-list.component';

describe('MyChargingStationListComponent', () => {
  let component: MyChargingStationListComponent;
  let fixture: ComponentFixture<MyChargingStationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyChargingStationListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyChargingStationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
