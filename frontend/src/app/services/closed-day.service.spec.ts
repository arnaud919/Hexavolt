import { TestBed } from '@angular/core/testing';

import { ClosedDayService } from './closed-day.service';

describe('ClosedDayService', () => {
  let service: ClosedDayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClosedDayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
