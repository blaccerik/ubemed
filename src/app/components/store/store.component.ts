import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";
import {StoreService} from "../services/store.service";
import {Post} from "../model/Post";
import {HttpClient} from "@angular/common/http";
import {Product} from "../model/Product";
import {Router} from "@angular/router";

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.css']
})
export class StoreComponent implements OnInit {

  products: Product[] = [];

  constructor(
    private router: Router,
    private storeService: StoreService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.storeService.getAll().subscribe(
      next => {
        // if (posts) {
        //   hideloader();
        // }
        console.log(next)
        this.products = next
      }
      );
  }

  create() {
    this.router.navigate(["store/new"])
  }

  image(array: any) {
    return  'data:image/jpeg;base64,' + array;
  }
}
