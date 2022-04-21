import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {StoreService} from "../services/store.service";
import {Product} from "../model/Product";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {StoreOfferComponent} from "./store-offer/store-offer.component";
import {NgForm} from "@angular/forms";

interface Event {
  value: string;
  viewValue: string;
  icon: string;
}


@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss']
})
export class StoreComponent implements OnInit {

  products: Product[] = [];
  events: Event[] = [
    {value: 'hot', viewValue: 'Hot', icon: "whatshot"},
    {value: 'expensive', viewValue: 'Expensive', icon: "attach_money"},
    {value: 'cheap', viewValue: 'Cheap', icon: "money_off"},
    {value: 'new', viewValue: 'New', icon: "new_releases"},
  ];


  constructor(
    private router: Router,
    private storeService: StoreService,
    public authService: AuthService,
    private dialog: MatDialog,
  ) {}


  onClickEvent(value: string) {
    console.log(value)
  }

  refresh() {
    this.ngOnInit();
  }

  search(form: NgForm) {
    console.log(form.value.search)
  }

  ngOnInit(): void {

    // @ts-ignore
    document.getElementById('loading').style.display = "";

    // @ts-ignore
    document.getElementById('grid').style.display = "none";

    this.storeService.getAll().subscribe(
      next => {
        if (next) {
          hideloader();
        }
        this.products = next

        // get images
        for (let i = 0; i < this.products.length; i++) {
          let product = this.products[i];
          const value = localStorage.getItem("image-" + product.id)

          // save to localstorage
          if (!value) {
            this.storeService.getImg(product.id).subscribe(
              (next: any) => {
                product.file = next.file;
                localStorage.setItem("image-" + product.id, product.file);
              }
            )
          } else {
            product.file = value;
          }
        }
      }
      );
    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";
      // @ts-ignore
      document.getElementById('grid').style.display = "";
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
    // console.log(array)
    return  'data:image/jpeg;base64,' + array;
  }
}
