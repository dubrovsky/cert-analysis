import { TestBed } from '@angular/core/testing';
import {CertificateDetailsResolverService} from "./certificate-details-resolver.service";


describe('CertificateViewResolverService', () => {
  let service: CertificateDetailsResolverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CertificateDetailsResolverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
