import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CasinoPopupComponent } from './casino-popup.component';

describe('CasinoPopupComponent', () => {
  let component: CasinoPopupComponent;
  let fixture: ComponentFixture<CasinoPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CasinoPopupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CasinoPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
