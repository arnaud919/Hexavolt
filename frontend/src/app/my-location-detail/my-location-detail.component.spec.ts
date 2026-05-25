import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLocationDetailComponent } from './my-location-detail.component';

describe('MyLocationDetailComponent', () => {
  let component: MyLocationDetailComponent;
  let fixture: ComponentFixture<MyLocationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyLocationDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyLocationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
