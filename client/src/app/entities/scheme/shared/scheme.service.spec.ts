import { TestBed } from '@angular/core/testing';

import { SchemeService } from './scheme.service';

describe('SchemeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SchemeService = TestBed.get(SchemeService);
    expect(service).toBeTruthy();
  });
});
