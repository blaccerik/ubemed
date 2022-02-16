import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoreOfferComponent } from './store-offer.component';

describe('StoreOfferComponent', () => {
  let component: StoreOfferComponent;
  let fixture: ComponentFixture<StoreOfferComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoreOfferComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoreOfferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
