import { TestBed } from '@angular/core/testing';

import { FileResolverService } from './file-resolver.service';

describe('FileResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FileResolverService = TestBed.get(FileResolverService);
    expect(service).toBeTruthy();
  });
});
