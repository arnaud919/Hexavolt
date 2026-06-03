import { TestBed } from '@angular/core/testing';

import { StatusChargingStationServiceService } from './status-charging-station.service.service';

describe('StatusChargingStationServiceService', () => {
  let service: StatusChargingStationServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatusChargingStationServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
