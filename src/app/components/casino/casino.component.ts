import { Component, OnInit } from '@angular/core';
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
  prevNumber: number;

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
    this.prevNumber = 0;
    this.spinning = false;
    this.startAngle = 0
    this.connect()
    const source = interval(1000);
    source.subscribe(() => this.time());
    this.drawRouletteWheel(this.get());
  }

  time() {
    if (this.date !== undefined) {
      let date: Date = new Date();
      let sec = Math.floor((date.getTime() - this.date.getTime()) / 1000);
      // this.calculate();
      if (sec <= 60) {
        this.seconds = 60 - sec;
      }
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
            this.spinning = true;
            let list = this.get().slice();
            this.startSpin(w, list);

            // this.stopRotateWheel();
            this.map.clear();
            this.players.clear();
            this.value = 0;
            // this.wheelEnterBroadcasts = [];
            this.reset();
          } else {
            this.value += w.value;
            this.updatePlayers(w);
            if (this.prevNumber != this.get().length) {
              this.prevNumber = this.get().length;
              if (!this.spinning) {
                this.drawRouletteWheel(this.get());
              }
            }
          }
        })
        this.reset();
      }
    );
  }

  // spinAngleStart = 0;
  // spinTime = 0;
  // spinTimeTotal: number;
  // arc = 0;
  endAngle: number;
  halfAngle: number;
  spinAngle: number;
  startAngle: number;
  ctx: any;
  spinTimerID: any;
  spinning: boolean;
  // regions: region[];


  colors = ["#62c48d", "#b0a449", "#4085c9"];
  // restaraunts = ["0", "32", "15", "19"];

  drawRouletteWheel(list: WheelEnterBroadcast[]) {

    // const segmentCount = 21;
    //
    // let initializeWheel = function() {
    //   let canvas = document.getElementById("wheelcanvas");
    //   // @ts-ignore
    //   let ctx = canvas.getContext("2d");
    //   let size = 500
    //   // @ts-ignore
    //   canvas.width = canvas.height = size;
    //   // @ts-ignore
    //   ctx.fillStyle = "red";
    //   // @ts-ignore
    //   ctx.fillRect(0, 0, size, size);
    //   let r = 200;// the radius of the wheel
    //   let degrees = 2*Math.PI / segmentCount;
    //   function drawSegment(){
    //     // @ts-ignore
    //     ctx.fillStyle = "black";
    //     // @ts-ignore
    //     ctx.strokeStyle = "white";
    //     // @ts-ignore
    //     ctx.moveTo(0,0);
    //     // @ts-ignore
    //     ctx.lineTo(r,0);
    //     // @ts-ignore
    //     ctx.arc(0,0,r,0,degrees);
    //     // @ts-ignore
    //     ctx.closePath();
    //     // @ts-ignore
    //     ctx.fill();
    //     // @ts-ignore
    //     ctx.stroke();
    //   }
    //   // @ts-ignore
    //   ctx.translate(size/2,size/2); // translate the context in the center.
    //   for (let segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
    //     drawSegment()
    //     // @ts-ignore
    //     ctx.rotate(degrees);
    //   }
    //   // canvas.appendTo(document.body)
    // }
    //
    // initializeWheel()


    let canvas = document.getElementById("wheelcanvas");
    // @ts-ignore
    if (canvas.getContext) {
      let outsideRadius = 200;
      let textRadius = 160;
      let insideRadius = 120;

      // @ts-ignore
      this.ctx = canvas.getContext("2d");
      this.ctx.clearRect(0,0,500,500);


      this.ctx.strokeStyle = "black";
      this.ctx.lineWidth = 2;

      this.ctx.font = 'bold 12px sans-serif';


      let n = list.length
      let fill = true;
      if (n == 0) {
        n = 1;
        fill = false;
      }
      let arc = 2 * Math.PI / n;
      for(let i = 0; i < n; i++) {
        let angle = this.startAngle + i * arc - Math.PI / 2;
        this.ctx.fillStyle = this.colors[i % this.colors.length];

        this.ctx.beginPath();
        this.ctx.arc(250, 250, outsideRadius, angle, angle + arc, false);
        this.ctx.arc(250, 250, insideRadius, angle  + arc, angle, true);
        this.ctx.stroke();
        this.ctx.fill();

        this.ctx.save();
        // this.ctx.shadowOffsetX = -1;
        // this.ctx.shadowOffsetY = -1;
        // this.ctx.shadowBlur    = 0;
        // this.ctx.shadowColor   = "rgb(220,220,220)";
        this.ctx.fillStyle = "black";
        this.ctx.translate(250 + Math.cos(angle + arc / 2) * textRadius, 250 + Math.sin(angle + arc / 2) * textRadius);
        this.ctx.rotate(angle + arc / 2 + Math.PI / 2);

        let text = ""
        if (fill) {
          text = list[i].name;
        }
        this.ctx.fillText(text, -this.ctx.measureText(text).width / 2, 0);
        this.ctx.restore();
      }

      //Arrow
      this.ctx.fillStyle = "black";
      this.ctx.beginPath();
      this.ctx.moveTo(250 - 4, 250 - (outsideRadius + 5));
      this.ctx.lineTo(250 + 4, 250 - (outsideRadius + 5));
      this.ctx.lineTo(250 + 4, 250 - (outsideRadius - 5));
      this.ctx.lineTo(250 + 9, 250 - (outsideRadius - 5));
      this.ctx.lineTo(250 + 0, 250 - (outsideRadius - 13));
      this.ctx.lineTo(250 - 9, 250 - (outsideRadius - 5));
      this.ctx.lineTo(250 - 4, 250 - (outsideRadius - 5));
      this.ctx.lineTo(250 - 4, 250 - (outsideRadius + 5));
      this.ctx.fill();
    }
  }


  startSpin(winner: WheelEnterBroadcast, list: WheelEnterBroadcast[]) {
    clearInterval(this.spinTimerID);

    let spins = 3;

    // this.regions = [];
    // this.spinTime = 0;
    // this.spinAngleStart = 0;
    this.startAngle = 0;
    this.endAngle = 0;
    this.spinAngle = 10;

    // this.spinTimeTotal = 1 * 1080;
    // this.calculate();


    // let list = this.get();
    let size = 2 * Math.PI / list.length;
    for (let i = 0; i < list.length; i++) {
      let element = list[i];
      if (element.name === winner.name) {
        this.endAngle = (spins * 2 * Math.PI) - (size * i) - size / 2;
        this.halfAngle = this.endAngle - 3 * Math.PI
        break
      }
    }
    this.drawRouletteWheel(list);

    this.spinTimerID = setInterval(() => {
        this.rotateWheel(list, winner);
      },
      30
    );
    // this.rotateWheel(10, this.get());
  }

  resetWheel() {
    this.startAngle = 0;
    this.spinning = false;
    this.prevNumber = 0;
    this.drawRouletteWheel(this.get());
  }

  rotateWheel(list: WheelEnterBroadcast[], winner: WheelEnterBroadcast) {
    // this.spinTime += 30;
    // if(this.spinTime > this.spinTimeTotal) {
    //   this.stopRotateWheel();
    //   return;
    // }

    // if (this.spinTime >= 2000) {
    //   nr = -1;
    // }
    // } else {
    //   spinAngle = this.spinAngleStart - this.easeOut(this.spinTime, 0, this.spinAngleStart, this.spinTimeTotal);
    // }

    console.log(this.spinAngle)

    if (this.halfAngle < this.startAngle) {
      // 2 0.1409
      if (this.spinAngle > 0.05) {
        this.spinAngle -= 0.0935;
      }
      if (this.spinAngle < 0.05) {
        this.spinAngle = 0.05;
      }
    }

    this.startAngle += (this.spinAngle * Math.PI / 180);
    // console.log(this.startAngle)
    // this.startAngle = this.startAngle % (2 * Math.PI)

    if (this.endAngle < this.startAngle) {
      this.startAngle = this.endAngle
      clearTimeout(this.spinTimerID);
      alert("winner " + winner.name)

      setTimeout( () => {
        this.resetWheel();
        },
        1000
      )
    }

    this.drawRouletteWheel(list);

    // this.spinTimeout = setTimeout(() => {
    //   this.rotateWheel(nr, list);
    // },30);

    // console.log(this.startAngle)
  }

  // stopRotateWheel() {
  //   // this.spinTimeout.
  //   // clearTimeout(this.spinTimeout);
  //
  //   // for (let i = 0; i < this.regions.length; i++) {
  //   //   let region = this.regions[i];
  //   //   if (region.name === this.winner.name) {
  //   //     angle = region.angle
  //   //     break;
  //   //   }
  //   // }
  //
  //   // let winner = "";
  //   // let angle = 0;
  //   // for (let i = 0; i < this.regions.length; i++) {
  //   //   let region = this.regions[i];
  //   //   if (region.name === this.winner.name) {
  //   //     this.endAngle = region.angle
  //   //     br1eak;
  //   //   }
  //   // }
  //   //
  //   //
  //   // // let degrees = this.startAngle * 180 / Math.PI + 90;
  //   // // let arcd = this.arc * 180 / Math.PI;
  //   // // let index = Math.floor((360 - degrees % 360) / arcd);
  //   // this.ctx.save();
  //   // this.ctx.font = 'bold 30px sans-serif';
  //   // let text = this.winner.name;
  //   // this.ctx.fillText(text, 250 - this.ctx.measureText(text).width / 2, 250 + 10);
  //   // this.ctx.restore();
  // }

  easeOut(t : any, b : any, c : any, d : any): number {
    let ts = (t/=d)*t;
    let tc = ts*t;
    return b+c*(tc + -3*ts + 3*t);
  }

  // calculate() {
  //   let list: WheelEnterBroadcast[] = []
  //   let players = this.get()
  //   if (players.length > 0) {
  //     for (let i = 0; i < players.length; i++) {
  //       let player = players[i];
  //       let percent = Math.round(player.value / this.value * 20);
  //       for (let j = 0; j < percent; j++) {
  //         if (list.length < 20) {
  //           list.push(player);
  //         }
  //       }
  //     }
  //     while (list.length != 20) {
  //       const random = players[Math.floor(Math.random() * players.length)];
  //       list.push(random);
  //     }
  //
  //     for (let i = list.length - 1; i > 0; i--) {
  //       let j = Math.floor(Math.random() * (i + 1));
  //       let temp = list[i];
  //       list[i] = list[j];
  //       list[j] = temp;
  //     }
  //   }
  //   this.randomList = list;
  // }


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
