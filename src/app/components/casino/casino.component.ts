import { Component, OnInit } from '@angular/core';
import * as SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
import {BidResponse} from "../model/BidResponse";
import {FormControl, Validators} from "@angular/forms";
import {WheelEnterBroadcast} from "../model/WheelEnterBroadcast";
import {CasinoService} from "../services/casino.service";
import {interval} from "rxjs";

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
  winner: WheelEnterBroadcast;
  map: Map<number, WheelEnterBroadcast>;
  players: Map<string, WheelEnterBroadcast>;

  randomList: WheelEnterBroadcast[];

  list: WheelEnterBroadcast[];
  list2: WheelEnterBroadcast[];
  list3: WheelEnterBroadcast[];

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
    this.randomList = [];
    this.list = [];
    this.list2 = [];
    this.list3 = [];
    this.map = new Map();
    this.players = new Map();
    this.connect()

    const source = interval(1000);
    source.subscribe(val => this.time());

  }

  calculate() {
      let list: WheelEnterBroadcast[] = []
      let players = this.get()
      if (players.length > 0) {
          for (let i = 0; i < players.length; i++) {
              let player = players[i];
              let percent = Math.round(player.value / this.value * 20);
              for (let j = 0; j < percent; j++) {
                  if (list.length < 20) {
                      list.push(player);
                  }
              }
          }
          while (list.length != 20) {
              const random = players[Math.floor(Math.random() * players.length)];
              list.push(random);
          }

          for (let i = list.length - 1; i > 0; i--) {
              let j = Math.floor(Math.random() * (i + 1));
              let temp = list[i];
              list[i] = list[j];
              list[j] = temp;
          }
      }
      this.randomList = list;
  }

  time() {
    if (this.date !== undefined) {
      let date: Date = new Date();
      let sec = Math.floor((date.getTime() - this.date.getTime()) / 1000);


      let elements = document.getElementsByClassName('test')
      console.log(elements)
      for (let i = 0; i < elements.length; i++) {
        let el = elements[i]
        // console.log(el.style.left)
        // @ts-ignore
        el.style.left = '5px'
        // console.log(el.style.left)
        break
        // el.style.left = parseFloat(el.style.left || 0) - 5 + 'px';
      }

      if (sec % 1 == 0) {
          this.calculate();
      }
      if (sec <= 60) {
        this.seconds = 60 - sec;
      }
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
            w.mid = res2.mid;

            if (w.coins <= 0 && w.value <= 0) {
              w.value = -w.value
              w.coins = -w.coins
              this.winner = w
              this.map.clear();
              this.players.clear();
              this.value = 0;
              // this.wheelEnterBroadcasts = [];
              this.reset();
            } else {
              this.value += w.value
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
