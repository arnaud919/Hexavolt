import { TestBed } from '@angular/core/testing';

import { StatusChargingStationService } from './status-charging-station.service.service';

describe('StatusChargingStationServiceService', () => {
  let service: StatusChargingStationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatusChargingStationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
