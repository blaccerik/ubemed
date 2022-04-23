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

@Component({
  selector: 'app-store-offer',
  templateUrl: './store-offer.component.html',
  styleUrls: ['./store-offer.component.scss']
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
    // this.connect();
    // this.dialogRef.afterClosed().subscribe(result => {
    //   this.disconnect();
    //   // console.log(`Dialog result: ${result}`); // Pizza!
    // });

  }

  // connect() {
  //   const socket = new SockJS('api/websocket');
  //   this.stompClient = Stomp.over(socket);
  //   const headers = {}
  //   this.stompClient.connect(
  //     headers,
  //     (next: any) => {
  //
  //       this.storeService.getBids(this.id).subscribe(res => {
  //         res.sort(compare)
  //         this.bids = res;
  //         if (this.bids.length > 0) {
  //           this.cost = this.bids[0].amount
  //           this.form.setControl("amount",
  //             new FormControl(' ', [
  //               Validators.required,
  //               Validators.max(this.authService.getCoins()),
  //               Validators.min(this.cost + 1)
  //             ])
  //           )
  //         }
  //       })
  //       this.showLoader = false;
  //       this.stompClient.subscribe('/bids/' + this.id, (res: any) => {
  //         const value = JSON.parse(res.body)
  //         this.cost = value.amount;
  //         this.form.setControl("amount",
  //           new FormControl(' ', [
  //             Validators.required,
  //             Validators.max(this.authService.getCoins()),
  //             Validators.min(this.cost + 1)
  //           ])
  //         )
  //         const bidResponse: BidResponse = new BidResponse(value.username, value.amount)
  //         this.bids.unshift(bidResponse);
  //       })
  //     }
  //   );
  //
  //   function compare( a: BidResponse, b: BidResponse ) {
  //     if ( a.amount < b.amount ){
  //       return 1;
  //     }
  //     if ( a.amount > b.amount ){
  //       return -1;
  //     }
  //     return 0;
  //   }
  // }
  //
  // disconnect() {
  //   if (this.stompClient != null) {
  //     this.stompClient.disconnect();
  //   }
  // }

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
