import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {CasinoWheelPlayer} from "../../model/CasinoWheelPlayer";

@Component({
  selector: 'app-casino-popup',
  templateUrl: './casino-popup.component.html',
  styleUrls: ['./casino-popup.component.scss']
})
export class CasinoPopupComponent implements OnInit {

  winner: CasinoWheelPlayer

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {winner: CasinoWheelPlayer},
  ) {
    this.winner = data.winner;
  }

  ngOnInit(): void {
  }

}
