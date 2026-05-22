import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyReservationDetailComponent } from './my-reservation-detail.component';

describe('MyReservationDetailComponent', () => {
  let component: MyReservationDetailComponent;
  let fixture: ComponentFixture<MyReservationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyReservationDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyReservationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
