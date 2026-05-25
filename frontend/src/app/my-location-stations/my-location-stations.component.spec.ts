import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLocationStationsComponent } from './my-location-stations.component';

describe('LocationStationsComponent', () => {
  let component: MyLocationStationsComponent;
  let fixture: ComponentFixture<MyLocationStationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyLocationStationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyLocationStationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
