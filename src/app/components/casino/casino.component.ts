import { Component, OnInit } from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {BidResponse} from "../model/BidResponse";
import {FormControl, Validators} from "@angular/forms";
import {WheelEnterBroadcast} from "../model/WheelEnterBroadcast";

@Component({
  selector: 'app-casino',
  templateUrl: './casino.component.html',
  styleUrls: ['./casino.component.scss']
})
export class CasinoComponent implements OnInit {

  stompClient: any;
  wheelEnterBroadcasts: WheelEnterBroadcast[];

  constructor() { }

  ngOnInit(): void {
    this.disconnect()

    // @ts-ignore
    document.getElementById('loading').style.display = "";

    // todo get prev values
    //  get time
    //

    this.wheelEnterBroadcasts = [];
    this.connect()
  }

  hideLoader() {
    // @ts-ignore
    document.getElementById('loading').style.display = "none";
  }


  connect() {
    const socket = new SockJS('api/websocket');
    // console.log(socket.url);
    // console.log("test")
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};

    // this.stompClient.connect({}, function(frame: any) {
    //   console.log("1", frame)
    // })
    const headers = {}
    this.stompClient.connect(
        headers,
        (next: any) => {
          this.hideLoader();
          this.stompClient.subscribe('/casino', (res: any) => {
            const res2 = JSON.parse(res.body)
            const w = new WheelEnterBroadcast();
            w.value = res2.value;
            w.coins = res2.coins;
            w.name = res2.name;

            if (w.coins <= 0 && w.value <= 0) {
              this.wheelEnterBroadcasts = [];
            } else {
              this.wheelEnterBroadcasts.push(w);
            }
            console.log(this.wheelEnterBroadcasts)
          })
        }
    );
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }
  }
}
