import {Component, OnInit, Renderer2} from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {BidResponse} from "../model/BidResponse";
import {FormControl, Validators} from "@angular/forms";
import {WheelEnterBroadcast} from "../model/WheelEnterBroadcast";
import {CasinoService} from "../services/casino.service";
import {interval} from "rxjs";
import {CasinoWheelPlayer} from "../model/CasinoWheelPlayer";
import {StoreOfferComponent} from "../store/store-offer/store-offer.component";
import {MatDialog} from "@angular/material/dialog";
import {CasinoPopupComponent} from "./casino-popup/casino-popup.component";

interface region {
  name: string;
  angle: number;
}

interface player {
  name: string;
}

@Component({
  selector: 'app-casino',
  templateUrl: './casino.component.html',
  styleUrls: ['./casino.component.scss']
})
export class CasinoComponent implements OnInit {

  stompClient: any;
  date: Date;
  totalValue: number;
  seconds: number;
  spinner: CasinoWheelPlayer[];

  map: Map<number, WheelEnterBroadcast>;
  players: Map<string, WheelEnterBroadcast>;
  winner: CasinoWheelPlayer | undefined;
  topPlayer: WheelEnterBroadcast | undefined;

  constructor(
    private casinoService: CasinoService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.disconnect()

    // @ts-ignore
    document.getElementById('loading').style.display = "";

    this.totalValue = 0;
    this.seconds = -1;

    this.map = new Map();
    this.players = new Map();
    this.spinner = [];
    this.winner = undefined;

    this.connect()
    const source = interval(1000);
    source.subscribe(() => this.time());
  }

  time() {
    if (this.date !== undefined) {
      let date: Date = new Date();
      let sec = Math.floor((date.getTime() - this.date.getTime()) / 1000);
      if (sec <= 10) {
        this.seconds = 10 - sec;
      }
    }
  }

  hideLoader() {
    // @ts-ignore
    document.getElementById('loading').style.display = "none";
  }


  connect() {
    const socket = new SockJS('api/websocket');
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = () => {};
    const headers = {};
    this.stompClient.connect(
      headers,
      (next: any) => {
        this.hideLoader();
        this.stompClient.subscribe('/casino', (res: any) => {
          const res2 = JSON.parse(res.body);
          const w = new WheelEnterBroadcast();
          w.value = res2.value;
          w.mid = res2.mid;
          w.players = res2.players
          w.isNormalBroadcast = res2.normalBroadcast
          w.user = res2.user

          // winning broadcast
          if (!w.isNormalBroadcast) {
            this.reset();
            this.spinner = w.players;  // auto spins the players
            let winner = this.spinner[22];

            // set delay to show winner
            setTimeout(() =>{
              this.winner = winner;

              // open popup
              const dialogRef = this.dialog.open(CasinoPopupComponent, {
                data: {winner : this.winner}
              });

              // close popup after some time
              dialogRef.afterOpened().subscribe(_ => {
                setTimeout(() => {
                  dialogRef.close();
                }, 1000)
              })
            }, 3000);

          } else {
            this.spinner = []
            this.winner = undefined;
            this.updatePlayers(w);
          }
        })
        this.reset();
      }
    );
  }

  updatePlayers(w: WheelEnterBroadcast) {
    if (!this.map.has(w.mid)) {
      this.map.set(w.mid, w)
      let coins = this.players.get(w.user.name);
      if (coins) {
        w.value += coins.value
        this.players.set(w.user.name, w);
      } else {
        this.players.set(w.user.name, w);
      }

      // check if top
      if (this.topPlayer === undefined || w.value > this.topPlayer.value) {
        this.topPlayer = w;
      }
    }
    let value = 0;
    for (let o of this.players.values()){
      value += o.value
    }
    this.totalValue = value
  }


  reset() {
    this.map.clear();
    this.players.clear();
    this.topPlayer = undefined;
    this.totalValue = 0;
    this.casinoService.getData().subscribe(
      next => {
        this.date = new Date(next.date)
        for (let i = 0; i < next.list.length; i++) {
          let w = next.list[i];
          this.updatePlayers(w);
        }

      }
    )
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }
  }

  getPlayers() {
    return Array.from(this.players.values());
  }
}
