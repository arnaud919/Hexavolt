import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLocationCreateComponent } from './my-location-create.component';

describe('MyLocationCreateComponent', () => {
  let component: MyLocationCreateComponent;
  let fixture: ComponentFixture<MyLocationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyLocationCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyLocationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
