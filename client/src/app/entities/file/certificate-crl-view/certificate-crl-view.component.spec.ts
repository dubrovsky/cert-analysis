import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateCrlViewComponent } from './certificate-crl-view.component';

describe('CertificateCrlViewComponent', () => {
  let component: CertificateCrlViewComponent;
  let fixture: ComponentFixture<CertificateCrlViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CertificateCrlViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CertificateCrlViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
