import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FindStationComponent } from './find-station.component';

describe('FindStationComponent', () => {
  let component: FindStationComponent;
  let fixture: ComponentFixture<FindStationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FindStationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FindStationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
