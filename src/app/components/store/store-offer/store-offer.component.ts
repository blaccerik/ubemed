import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Stomp} from "@stomp/stompjs";
import * as SockJS from 'sockjs-client';
import {BidResponse} from "../../model/BidResponse";

@Component({
  selector: 'app-store-offer',
  templateUrl: './store-offer.component.html',
  styleUrls: ['./store-offer.component.css']
})
export class StoreOfferComponent implements OnInit {

  id: number
  cost: number;
  form: FormGroup;
  stompClient: any;
  bids: BidResponse[];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {id: number, cost: number},
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<StoreOfferComponent>
  ) {
    this.id = data.id;
    this.cost = data.cost;
    this.form = this.initForm();
    this.bids = [];
  }

  ngOnInit(): void {
    // connect to websocket
    // const socket = new SockJS('/api/websocket');
    // this.stompClient = Stomp.over(socket);
    // this.stompClient.connect();
    this.connect();
    this.dialogRef.afterClosed().subscribe(result => {
      this.disconnect();
      // console.log(`Dialog result: ${result}`); // Pizza!
    });

  }

  connect() {
    const socket = new SockJS('api/websocket');
    this.stompClient = Stomp.over(socket);
    const headers = {}
    this.stompClient.connect(
      headers,
      (next: any) => {
        this.stompClient.subscribe('/bids/' + this.id, (res: any) => {
          // console.log("res", res);
          const value = JSON.parse(res.body)
          const bidResponse: BidResponse = new BidResponse(value.username, value.amount)
          this.bids.push(bidResponse);
        })
        console.log(this.id)
      }
    );
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }
  }

  initForm() {
    return this.formBuilder.group({
      offer: new FormControl('',
        [Validators.required, Validators.min(this.cost + 1)]
      ),
    });
  }

  hasError(path: string, errorCode: string) {
    return this.form && this.form.hasError(errorCode, path);
  }

  submit() {
    // this.dialogRef.close();
  }
}
