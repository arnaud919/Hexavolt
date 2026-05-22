import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StationPublicDetailComponent } from './station-public-detail.component';

describe('StationPublicDetailComponent', () => {
  let component: StationPublicDetailComponent;
  let fixture: ComponentFixture<StationPublicDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StationPublicDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StationPublicDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
