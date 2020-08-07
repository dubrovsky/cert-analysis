import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CrlDetailsComponent } from './crl-details.component';

describe('CrlDetailsComponent', () => {
  let component: CrlDetailsComponent;
  let fixture: ComponentFixture<CrlDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CrlDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CrlDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
