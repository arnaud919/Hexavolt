import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLocationListComponent } from './my-location-list.component';

describe('MyLocationListComponent', () => {
  let component: MyLocationListComponent;
  let fixture: ComponentFixture<MyLocationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyLocationListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyLocationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
