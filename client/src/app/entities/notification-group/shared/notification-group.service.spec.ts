import { TestBed } from '@angular/core/testing';

import { NotificationGroupService } from './notification-group.service';

describe('NotificationGroupService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NotificationGroupService = TestBed.get(NotificationGroupService);
    expect(service).toBeTruthy();
  });
});
