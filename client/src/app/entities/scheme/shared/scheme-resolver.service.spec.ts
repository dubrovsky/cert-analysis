import { TestBed } from '@angular/core/testing';

import { SchemeResolverService } from './scheme-resolver.service';

describe('SchemeResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SchemeResolverService = TestBed.get(SchemeResolverService);
    expect(service).toBeTruthy();
  });
});
