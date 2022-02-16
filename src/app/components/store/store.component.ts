import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {StoreService} from "../services/store.service";
import {Post} from "../model/Post";
import {HttpClient} from "@angular/common/http";
import {Product} from "../model/Product";
import {Router} from "@angular/router";
import {LoginComponent} from "../login/login.component";
import {MatDialog} from "@angular/material/dialog";
import {StoreOfferComponent} from "./store-offer/store-offer.component";

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit {

  products: Product[] = [];

  constructor(
    private router: Router,
    private storeService: StoreService,
    public authService: AuthService,
    private dialog: MatDialog,
  ) {}

  ran() {
    return Math.random() < 0.5;
  }

  ngOnInit(): void {
    this.storeService.getAll().subscribe(
      next => {
        if (next) {
          hideloader();
        }
        // console.log(next)
        this.products = next
      }
      );
    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
    }
  }

  create() {
    this.router.navigate(["store/new"])
  }

  makeOffer(id: number, offer: number) {
    this.dialog.open(StoreOfferComponent, {
      data: {
        id: id,
        cost: offer
      }
    });
  }

  image(array: any) {
    return  'data:image/jpeg;base64,' + array;
  }
}
