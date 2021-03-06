import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {StoreService} from "../services/store.service";
import * as SockJS from 'sockjs-client';
import {Product} from "../model/Product";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {StoreOfferComponent} from "./store-offer/store-offer.component";
import {FormBuilder, FormControl, NgForm, Validators} from "@angular/forms";
import {BidResponse} from "../model/BidResponse";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {InventoryService} from "../services/inventory.service";
import {Stomp} from "@stomp/stompjs";
// import SockJS = require("sockjs-client");

interface Event {
  value: string;
  viewValue: string;
  icon: string;
}

interface Cat {
  value: number,
  viewValue: string;
  selected: boolean;
}


@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss'],
  animations: [
    trigger('valueAnimation', [
      transition(':increment', [
          style({ color: 'green', fontSize: '20px' }),
          animate('0.8s ease-out', style('*'))
        ]
      )
    ])
  ]
})
export class StoreComponent implements OnInit {

  products: Product[];
  events: Event[] = [
    {value: 'hot', viewValue: 'Hot', icon: "whatshot"},
    {value: 'expensive', viewValue: 'Expensive', icon: "attach_money"},
    {value: 'cheap', viewValue: 'Cheap', icon: "money_off"},
    {value: 'new', viewValue: 'New', icon: "new_releases"},
  ];

  cats: Cat[] = [
    {value: 1, viewValue: '1', selected: true},
    {value: 2, viewValue: '2', selected: true},
    {value: 3, viewValue: '3', selected: true},
    {value: 4, viewValue: '4', selected: true},
    {value: 5, viewValue: '5', selected: true},
    {value: 6, viewValue: '6', selected: true},
  ]

  stompClient: any;

  constructor(
    private router: Router,
    private storeService: StoreService,
    public authService: AuthService,
    private dialog: MatDialog,
    private formBuilder: FormBuilder
  ) {}


  onClickEvent(value: string) {
    this.router.navigate(["store"], {
      queryParams: {'filter': value}, queryParamsHandling: 'merge'
    }).then(r => this.ngOnInit());
  }

  onClickCat($event: any, value: number) {

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

  getSelected(): Product[] {
    let list: Product[] = []
    for (let i = 0; i < this.products.length; i++) {
      let product = this.products[i];
      for (let j = 0; j < this.cats.length; j++) {
        let cat = this.cats[j];
        if (cat.selected && product.cats.includes(cat.value)) {
          list.push(product);
          break
        }
      }
    }
    return list;
  }

  search(form: NgForm) {
    this.router.navigate(["store"], {
      queryParams: {'search': form.value.search},
      queryParamsHandling: 'merge'
    }).then(r => this.ngOnInit());
  }

  ngOnInit(): void {

    this.disconnect()

    // @ts-ignore
    document.getElementById('loading').style.display = "";

    if (document.getElementById('grid')) {
      // @ts-ignore
      document.getElementById('grid').style.display = "none";
    }

    if (document.getElementById('no-found')) {
      // @ts-ignore
      document.getElementById('no-found').style.display = "none";
    }


    this.connect()

    this.storeService.getAll().subscribe(
      next => {
        if (next) {
          hideloader();
        }
        this.products = next

        // get images
        for (let i = 0; i < this.products.length; i++) {
          let product = this.products[i];

          product.form = this.initForm(product.bid + 1);
          this.storeService.getImage(product);
          product.bids.sort(compare)
        }
      }
      );

    function compare( a: BidResponse, b: BidResponse ) {
      if ( a.amount < b.amount ){
        return 1;
      }
      if ( a.amount > b.amount ){
        return -1;
      }
      return 0;
    }

    function hideloader() {
      // @ts-ignore
      document.getElementById('loading').style.display = "none";

      if (document.getElementById('grid')) {
        // @ts-ignore
        document.getElementById('grid').style.display = "";
      }

      if (document.getElementById('no-found')) {
        // @ts-ignore
        document.getElementById('no-found').style.display = "";
      }
    }
  }

  initForm(amount: number) {
    return this.formBuilder.group({
      amount: new FormControl('',
        [
          Validators.required,
          Validators.pattern("^[0-9]*$"),
          Validators.min(amount)
        ]
      ),
    });
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
        this.stompClient.subscribe('/bids', (res: any) => {
          const value = JSON.parse(res.body)
          let username: string = value.username;
          let id: number = value.id;
          let amount: number = value.amount

          let val = this.products.find(function (el) {
            return el.id == id;
          });
          if (val !== undefined) {
            val.bid = amount;
            val.bids.unshift(new BidResponse(username, amount))

            // update validator
            val.form.setControl("amount",
              new FormControl(' ', [
                Validators.pattern("^[0-9]*$"),
                Validators.required,
                Validators.min(amount + 1)
              ])
            )
          }
          // console.log(val)
          // const bidResponse: BidResponse = new BidResponse(value.username, value.amount)
          // this.bids.unshift(bidResponse);
        })
      }
    );
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
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
