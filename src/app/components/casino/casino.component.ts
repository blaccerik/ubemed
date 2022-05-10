import {Component, OnInit, Renderer2} from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {BidResponse} from "../model/BidResponse";
import {FormControl, Validators} from "@angular/forms";
import {WheelEnterBroadcast} from "../model/WheelEnterBroadcast";
import {CasinoService} from "../services/casino.service";
import {interval} from "rxjs";

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
  // wheelEnterBroadcasts: WheelEnterBroadcast[];
  date: Date;
  value: number;
  seconds: number;
  // winner: WheelEnterBroadcast;
  map: Map<number, WheelEnterBroadcast>;
  players: Map<string, WheelEnterBroadcast>;
  // prevNumber: number;

  playerList: player[];
  playerNumber = 6;

  // randomList: WheelEnterBroadcast[];
  // list: WheelEnterBroadcast[];
  // list2: WheelEnterBroadcast[];
  // list3: WheelEnterBroadcast[];

  constructor(
      private casinoService: CasinoService
  ) { }

  ngOnInit(): void {
    this.disconnect()

    // @ts-ignore
    document.getElementById('loading').style.display = "";

    // todo get prev values
    //  get time
    //
    this.value = 0;
    this.seconds = -1;
    // this.randomList = [];
    // this.list = [];
    // this.list2 = [];
    // this.list3 = [];

    this.map = new Map();
    this.players = new Map();
    // this.prevNumber = 0;
    // this.spinning = false;
    // this.startAngle = 0
    this.connect()
    const source = interval(1000);
    source.subscribe(() => this.time());
    // this.drawRouletteWheel(this.get());

    this.playerList = [];
    setInterval(() => {
        this.move();
      },
      15
    );
    for (let i = 0; i < this.playerNumber; i++) {
      this.playerList.push(this.generatePlayer())
    }
  }

  nr = 1;
  generatePlayer(): player {
    if (this.players.size == 0) {
      return {name: 'demo'}
    }
    let players: WheelEnterBroadcast[] = Array.from(this.players.values());
    let player: WheelEnterBroadcast = players[Math.floor(Math.random() * players.length)];
    return {name: player.name}

  }

  els: any;
  cap = 10;
  move() {
    if (this.els === undefined) {
      let coords = Array.from({length: this.playerNumber}, (_, i) => i * (this.cap + 100))
      this.els = Array.from(document.getElementsByClassName('spinner-item') as HTMLCollectionOf<HTMLElement>)
      for (let i = 0; i < this.els.length; i++) {
        let el = this.els[i];
        el.style.left = coords[i] + 'px'
      }
    }
    for (let i = 0; i < this.els.length; i++) {
      let el = this.els[i];
      let left = el.offsetLeft
      if (left == -100) {
        // new block
        el.style.left = (this.playerNumber) * (100 + this.cap) - 100 + 'px';
        let playerBlock = this.playerList[i];
        playerBlock.name = this.generatePlayer().name;
      } else  {
        el.style.left = left - 1 + 'px';
      }
    }
  }

  time() {
    if (this.date !== undefined) {
      let date: Date = new Date();
      let sec = Math.floor((date.getTime() - this.date.getTime()) / 1000);
      // this.calculate();
      if (sec <= 60) {
        this.seconds = 60 - sec;
      }
      // this.renderer.appendChild(test.nativeElement, div);
      // if (sec == 60) {
      //   this.startSpin();
      // }
    }
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
    const headers = {};
    this.stompClient.connect(
      headers,
      (next: any) => {
        this.hideLoader();
        this.stompClient.subscribe('/casino', (res: any) => {
          const res2 = JSON.parse(res.body);
          const w = new WheelEnterBroadcast();
          w.value = res2.value;
          w.coins = res2.coins;
          w.name = res2.name;
          w.mid = res2.mid;

          // winning broadcast
          if (w.coins <= 0 && w.value <= 0) {
            w.value = -w.value;
            w.coins = -w.coins;
            // this.winner = w;
            // this.spinning = true;
            // let list = this.get().slice();
            // this.startSpin(w, list);

            // this.stopRotateWheel();
            this.map.clear();
            this.players.clear();
            this.value = 0;
            // this.wheelEnterBroadcasts = [];
            this.reset();
          } else {
            this.value += w.value;
            this.updatePlayers(w);
            // if (this.prevNumber != this.get().length) {
            //   this.prevNumber = this.get().length;
            //   if (!this.spinning) {
            //     this.drawRouletteWheel(this.get());
            //   }
            // }
          }
        })
        this.reset();
      }
    );
  }

  updatePlayers(w: WheelEnterBroadcast) {
    if (!this.map.has(w.mid)) {
      // console.log("e")
      this.map.set(w.mid, w)
      let coins = this.players.get(w.name);
      if (coins) {
        w.value += coins.value
        this.players.set(w.name, w);
      } else {
        this.players.set(w.name, w);
      }
    }
  }
  
  reset() {
    this.casinoService.getData().subscribe(
      next => {
        this.date = new Date(next.date)
        // this.value += next.value
        for (let i = 0; i < next.list.length; i++) {
          let w = next.list[i];
          if (!this.map.has(w.mid)) {
            this.value += w.value;
            this.updatePlayers(w);
          }
        }
      }
    )
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }
  }

  percent(nr: number) {
    return Math.floor(nr / this.value * 100);
  }

  get() {
    return Array.from(this.players.values());
  }
}
