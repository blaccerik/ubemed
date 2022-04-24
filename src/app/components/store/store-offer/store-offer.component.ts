import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Stomp} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';
import {BidResponse} from "../../model/BidResponse";
import {ForumService} from "../../services/forum.service";
import {StoreService} from "../../services/store.service";
import {AuthService} from "../../services/auth.service";
import {Product} from "../../model/Product";
import {StoreComponent} from "../store.component";
import {animate, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-store-offer',
  templateUrl: './store-offer.component.html',
  styleUrls: ['./store-offer.component.scss'],
  animations: [
    trigger('valueAnimation', [
      transition(':enter', [
          style({ color: 'green', fontSize: '20px' }),
          animate('0.8s ease-out', style('*'))
        ]
      )
    ])
  ]
})
export class StoreOfferComponent implements OnInit {

  product: Product;
  form: FormGroup;
  stompClient: any;
  bids: BidResponse[];
  showLoader: boolean;
  error: boolean;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {product: Product},
    private formBuilder: FormBuilder,
    private storeService: StoreService,
    private authService: AuthService,
    private dialogRef: MatDialogRef<StoreOfferComponent>
  ) {
    this.product = data.product;
    this.error = false;
    this.form = this.initForm();
    this.bids = [];
    this.showLoader = true;
  }

  ngOnInit(): void {

  }

  initForm() {
    return this.formBuilder.group({
      amount: new FormControl('',
        [
          Validators.required,
          Validators.max(this.authService.getCoins()),
          Validators.min(this.product.price + 1)
        ]
      ),
    });
  }

  hasError(path: string, errorCode: string) {
    return this.form && this.form.hasError(errorCode, path);
  }

  image(array: any) {
    return  'data:image/jpeg;base64,' + array;
  }

  submit() {
    // update Validator
    const post = { ...this.form.value}
    // const a = new FormData();
    // a.append("amount", this.form.value.amount);

    this.storeService.makeBid(this.product.id, post).subscribe(
      (next: any)=> {
        this.error = !next;
        if (next) {
          this.authService.updateCoins();
        }
      }
    )
  }
}
