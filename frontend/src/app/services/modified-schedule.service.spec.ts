import { TestBed } from '@angular/core/testing';

import { ModifiedScheduleService } from './modified-schedule.service';

describe('ModifiedScheduleService', () => {
  let service: ModifiedScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModifiedScheduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
