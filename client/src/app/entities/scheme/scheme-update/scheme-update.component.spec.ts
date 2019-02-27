import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SchemeUpdateComponent } from './scheme-update.component';

describe('SchemeUpdateComponent', () => {
  let component: SchemeUpdateComponent;
  let fixture: ComponentFixture<SchemeUpdateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SchemeUpdateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SchemeUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
