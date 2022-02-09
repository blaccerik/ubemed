import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StoreNewComponent } from './store-new.component';

describe('StoreNewComponent', () => {
  let component: StoreNewComponent;
  let fixture: ComponentFixture<StoreNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StoreNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StoreNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
