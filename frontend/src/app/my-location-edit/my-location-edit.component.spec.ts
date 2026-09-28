import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLocationEditComponent } from './my-location-edit.component';

describe('MyLocationEditComponent', () => {
  let component: MyLocationEditComponent;
  let fixture: ComponentFixture<MyLocationEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyLocationEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyLocationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
