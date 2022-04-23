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

interface Cat {
  value: string,
  viewValue: string;
  selected: boolean;
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

  cats: Cat[] = [
    {value: '1', viewValue: '1', selected: false},
    {value: '2', viewValue: '2', selected: false},
    {value: '3', viewValue: '3', selected: false},
    {value: '4', viewValue: '4', selected: false},
    {value: '5', viewValue: '5', selected: false},
  ]


  constructor(
    private router: Router,
    private storeService: StoreService,
    public authService: AuthService,
    private dialog: MatDialog,
  ) {}


  onClickEvent(value: string) {
    this.router.navigate(["store"], {queryParams: {'filter': value}, queryParamsHandling: 'merge'})
  }

  onClickCat($event: any, value: string) {

    $event.stopPropagation();

    for (let i = 0; i < this.cats.length; i++) {
      let cat = this.cats[i];
      if (cat.value == value) {
        cat.selected = !cat.selected
      }
    }
  }

  refresh() {
    this.ngOnInit();
  }

  search(form: NgForm) {
    this.router.navigate(["store"], {queryParams: {'search': form.value.search}, queryParamsHandling: 'merge'})
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

  makeOffer(product: Product) {
    this.dialog.open(StoreOfferComponent, {
      data: {
        product: product
      }
    });
  }

  image(array: any) {
    return  'data:image/jpeg;base64,' + array;
  }
}
