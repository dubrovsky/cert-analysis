import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeList00Component } from './scheme-list00.component';

describe('SchemeList00Component', () => {
  let component: SchemeList00Component;
  let fixture: ComponentFixture<SchemeList00Component>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchemeList00Component ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemeList00Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
