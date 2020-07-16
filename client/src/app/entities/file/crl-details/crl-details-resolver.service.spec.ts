import { TestBed } from '@angular/core/testing';
import {CrlDetailsResolverService} from "./crl-details-resolver.service";

describe('CrlViewResolverService', () => {
  let service: CrlDetailsResolverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CrlDetailsResolverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
