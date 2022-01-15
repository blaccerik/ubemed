import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuvepyksComponent } from './suvepyks.component';

describe('SuvepyksComponent', () => {
  let component: SuvepyksComponent;
  let fixture: ComponentFixture<SuvepyksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SuvepyksComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuvepyksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
